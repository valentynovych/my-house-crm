let tableLength = 5;
let currentPage = 0;

let byNumber = '';
let byDate = '';
let byStatus = '';
let byPaymentItemName = '';
let byOwner = '';
let byPersonalAccount = '';
let byPaymentType = '';
let timer;

const $filterByNumber = $('#filter-by-number');
const $filterByDate = $('#filter-by-date');
const $filterByStatus = $('#filter-by-status');
const $filterByPaymentItemName = $('#filter-by-payment-item-name');
const $filterByOwner = $('#filter-by-owner');
const $filterByPersonalAccount = $('#filter-by-personal-account');
const $filterByPaymentType = $('#filter-by-payment-type');

function getSheetStatusLabel(status) {
    switch (status) {
        case 'CONFIRMED':
            return cashSheetStatusConfirmed;
        case 'NOT_CONFIRMED' :
            return cashSheetStatusNotConfirmed;
        default:
            return '- - - -'
    }
}

$filterByDate.flatpickr();

$filterByStatus.select2({
    dropdownParent: $('#filter-by-status-wrap'),
    placeholder: '',
    allowClear: true,
    minimumResultsForSearch: -1,
    dropdownCssClass: 'select2-width',
    ajax: {
        type: "GET",
        url: 'cash-register/get-statuses',
        processResults: function (response) {
            return {
                results: $.map(response, function (status) {
                    return {
                        id: status,
                        text: getSheetStatusLabel(status)
                    }
                }),
            };
        }
    }
});

$filterByPaymentItemName.select2({
    dropdownParent: $('#filter-by-payment-item-name-wrap'),
    placeholder: '',
    allowClear: true,
    width: 'resolve',
    ajax: {
        type: "GET",
        url: 'system-settings/payment-items/get-items',
        data: function (params) {
            return {
                page: (params.page - 1) || 0,
                pageSize: 10
            };
        },
        processResults: function (response) {
            return {
                results: $.map(response.content, function (item) {
                    return {
                        id: item.id,
                        text: item.name
                    }
                }),
                pagination: {
                    more: !response.last
                }
            };
        }
    }
});

$filterByOwner.select2({
    debug: true,
    dropdownParent: $('#filter-by-owner-wrap'),
    placeholder: '',
    allowClear: true,
    ajax: {
        type: "GET",
        url: 'owners/get-owners',
        data: function (params) {
            return {
                fullName: params.term || '',
                page: (params.page - 1) || 0,
                pageSize: 10
            };
        },
        processResults: function (response) {
            return {
                results: $.map(response.content, function (owner) {
                    return {
                        id: owner.id,
                        text: owner.fullName
                    }
                }),
                pagination: {
                    more: !response.last
                }
            };
        }
    }
});

$filterByPersonalAccount.select2({
    debug: true,
    dropdownParent: $('#filter-by-personal-account-wrap'),
    placeholder: '',
    allowClear: true,
    ajax: {
        type: "GET",
        url: 'personal-accounts/get-personal-accounts',
        data: function (params) {
            return {
                accountNumber: params.term,
                page: (params.page - 1) || 0,
                pageSize: 10
            };
        },
        processResults: function (response) {
            console.log(response)
            return {
                results: $.map(response.content, function (account) {
                    return {
                        id: account.id,
                        text: decorateAccountNumber(account.accountNumber)
                    }
                }),
                pagination: {
                    more: !response.last
                }
            };
        }
    }
});

$filterByPaymentType.select2({
    debug: true,
    dropdownParent: $('#filter-by-payment-type-wrap'),
    minimumResultsForSearch: -1,
    placeholder: '',
    allowClear: true
});
$filterByPaymentType.val('').trigger('change');

$filterByNumber.on('input', function () {
    byNumber = this.value;
    delayBeforeSearch();
});
$filterByDate.on('change', function () {
    byDate = this.value;
    delayBeforeSearch();
});
$filterByStatus.on('change', function () {
    byStatus = this.value;
    delayBeforeSearch();
});

$filterByPaymentItemName.on('change', function () {
    byPaymentItemName = this.value;
    delayBeforeSearch();
});

$filterByOwner.on('change', function () {
    byOwner = this.value;
    delayBeforeSearch();
});
$filterByPersonalAccount.on('change', function () {
    byPersonalAccount = this.value;
    delayBeforeSearch();
});
$filterByPaymentType.on('change', function () {
    byPaymentType = this.value;
    delayBeforeSearch();
});

$('.clear-filters').on('click', function () {
    $filterByNumber.val('').trigger('input');
    $filterByDate.val('').trigger('change');
    $filterByStatus.val('').trigger('change');
    $filterByPaymentItemName.val('').trigger('change');
    $filterByOwner.val('').trigger('change');
    $filterByPersonalAccount.val('').trigger('change');
    $filterByPaymentType.val('').trigger('change');
})

function delayBeforeSearch() {
    let keyPause = 400;
    clearTimeout(timer);
    timer = setTimeout(function () {
        getSheets(0)
    }, keyPause);
}

$(window).on("load", function () {
    getSheets(currentPage);
    getPersonalAccountsStatistic();
})

function decorateAccountNumber(accountNumber) {
    let s = (accountNumber + '').padStart(10, '0000000000');
    return s.substring(0, 5) + '-' + s.substring(5, 10);
}

function getPersonalAccountsStatistic() {
    $.ajax({
        type: 'get',
        url: 'statistic/get-accounts-statistic',
        dataType: 'json',
        success: function (result) {
            fillStatistic(result);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}

function fillStatistic(stat) {
    $('#accounts-balance').html(`${stat.accountsBalanceOverpayments} ${currency}.`)
    $('#accounts-balance-arrears').html(`${stat.accountsBalanceArrears} ${currency}.`)

}

$('#export-to-exel').on('click', function () {
    let url = new URL('personal-accounts/export-to-excel', window.location.origin + window.location.pathname);
    url = addParametersToUrl(url);
    $.ajax({
        type: 'get',
        url: url,
        success: function (result) {
            const a = document.createElement('a');
            a.href = url;
            a.target = '_blank';
            a.click();
        },
        error: function () {
            //toastr.error(errorMessage)
        }
    })
    // this.href = url;
    // this.download = true;
    // this.target = '_blank';
})

function addParametersToUrl(url) {
    url.searchParams.append('page', currentPage);
    url.searchParams.append('pageSize', tableLength);
    if (byNumber) url.searchParams.append('sheetNumber', byNumber);
    if (byDate) url.searchParams.append('date', byStatus);
    if (byStatus) url.searchParams.append('status', byApartment);
    if (byPaymentItemName) url.searchParams.append('paymentItemName', byHouse);
    if (byOwner) url.searchParams.append('owner', bySection);
    if (byPersonalAccount) url.searchParams.append('personalAccount', byOwner);
    if (byPaymentType) url.searchParams.append('paymentType', byBalance);

    return url
}

function getSheets(page) {
    console.log('getSheets')
    blockCardDody();
    let url = new URL('cash-register/get-sheets', window.location.origin + window.location.pathname);
    url = addParametersToUrl(url);
    $.ajax({
        type: 'get',
        url: url,
        dataType: 'json',
        success: function (result) {
            console.log(result)
            currentPage = page;
            clearTableLine();
            $(".card-footer").children().remove();
            drawTable(result);
        },
        error: function () {
            //toastr.error(errorMessage);
        }
    });
}

function drawTable(result) {
    console.log('drawTable')
    // const page = result.pageable.pageNumber;
    if (result.content && result.content.length > 0) {
        console.log('drawTable1')
        let iter = 0;
        for (const personalAccount of result.content) {
            let accountNumber = personalAccount.accountNumber.toString().padStart(10, '0000000000');
            accountNumber = accountNumber.substring(0, 5) + '-' + accountNumber.substring(5, 10);
            const status = getSheetStatusLabel(personalAccount.status);
            const statusBadge = personalAccount.status === 'ACTIVE'
                ? `<span class="badge rounded-pill bg-success">${status}</span>`
                : personalAccount.status === 'NONACTIVE'
                    ? `<span class="badge rounded-pill bg-danger">${status}</span>`
                    : `<span class="badge rounded-pill bg-dark">${status}</span>`;
            let apartmentNumber = '-'
            let balanceText = '-';
            const isHaveApartment = !!personalAccount.apartment;
            if (isHaveApartment) {
                apartmentNumber = (personalAccount.apartment.apartmentNumber).toString().padStart(5, '00000');
                const numberFormat = new Intl.NumberFormat('uk');
                const balance = personalAccount.apartment.balance;
                balanceText = balance > 0
                    ? `<span class="text-success">${numberFormat.format(balance)}</span>`
                    : balance < 0
                        ? `<span class="text-danger">${numberFormat.format(balance)}</span>` : `<span class="text-dark">${numberFormat.format(balance)}</span>`;
            }

            $(`<tr data-href="personal-accounts/view-account/${personalAccount.id}" class="cursor-pointer">
            <td>${accountNumber}</td>
            <td class="text-center">${statusBadge}</td>
            <td>${apartmentNumber}</td>
            <td>${isHaveApartment ? personalAccount.apartment.house.name : '-'}</td>
            <td>${isHaveApartment ? personalAccount.apartment.section.name : '-'}</td>
            <td>${isHaveApartment ? personalAccount.apartment.owner.fullName : '-'}</td>
            <td class="text-center">${balanceText}</td>
            <td class="text-center">
              <div class="dropdown">
               <button type="button" class="btn p-0 dropdown-toggle hide-arrow"
                       data-bs-toggle="dropdown">
                <i class="ti ti-dots-vertical"></i>
               </button>
                 <div class="dropdown-menu">
                   <a class="dropdown-item" href="personal-accounts/edit-account/${personalAccount.id}">
                       <i class="ti ti-pencil me-1"></i>${buttonLabelEdit}
                   </a>
                   <button type="button" class="dropdown-item btn justify-content-start" 
                        data-bs-toggle="modal" data-bs-target="#modalToDelete" onclick="addDeleteEvent(${personalAccount.id})">
                       <i class="ti ti-trash me-1"></i>${buttonLabelDelete}</button>
                 </div>
              </div>
            </td>
            </tr>`
            ).appendTo("tbody");
            addListenerToRow();
        }
    } else {
        $(`<tr>
            <td colspan="9" class="text-center fw-bold h4">${emptyTableLabel}</td>
           </tr>`).appendTo('tbody');
    }

    drawPaginationElements(result, 'getSheets')
    drawPagination(result.totalPages, page, 'getSheets');

}

function addListenerToRow() {
    $('tr[data-href] td:not(:last-child)').on('click', function () {
        window.location = $(this).parent().attr('data-href');
    })
}

function addDeleteEvent(accountId) {
    $('.submit-delete').on('click', function () {
        if (accountId && accountId > 0) {

            $.ajax({
                type: 'delete',
                url: 'personal-accounts/delete/' + accountId,
                success: function () {
                    $('.close-modal').click();
                    toastr.success(successMessageOnDelete)
                    setTimeout(() => getSheets(currentPage), 400);
                },
                error: function () {
                    $('.close-modal').click()
                    toastr.error(errorMessageOnDelete);

                }
            })
        }
    })
}

function clearTableLine() {
    $("tbody").find("tr").each(function () {
        this.remove();
    });
}