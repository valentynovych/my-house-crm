let tableLength = 5;
let currentPage = 0;

let byNumber = '';
let byStatus = '';
let byApartment = '';
let byHouse = '';
let bySection = '';
let byOwner = '';
let byBalance = '';
let timer;

let $filterByNumber = $('#filter-by-number');
let $filterByStatus = $('#filter-by-status');
let $filterByApartment = $('#filter-by-apartment');
let $filterByHouse = $('#filter-by-house');
let $filterBySection = $('#filter-by-section');
let $filterByOwner = $('#filter-by-owner');
let $filterByBalance = $('#filter-by-balance');

function getAccountStatusLabel(status) {
    switch (status) {
        case 'ACTIVE':
            return accountStatusActive;
        case 'NONACTIVE' :
            return accountStatusNonActive;
        default:
            return '- - - -'
    }
}

$filterByStatus.select2({
    dropdownParent: $('#filter-by-status-wrap'),
    placeholder: '',
    allowClear: true,
    ajax: {
        type: "GET",
        url: 'personal-accounts/get-statuses',
        processResults: function (response) {
            return {
                results: $.map(response, function (status) {
                    return {
                        id: status,
                        text: getAccountStatusLabel(status)
                    }
                }),
            };
        }
    }
});

$filterByHouse.select2({
    dropdownParent: $('#filter-by-house-wrap'),
    placeholder: '',
    allowClear: true,
    ajax: {
        type: "GET",
        url: 'houses/get-houses',
        data: function (params) {
            return {
                name: params.term || '',
                page: (params.page - 1) || 0,
                pageSize: 10
            };
        },
        processResults: function (response) {
            return {
                results: $.map(response.content, function (house) {
                    return {
                        id: house.id,
                        text: house.name
                    }
                }),
                pagination: {
                    more: !response.last
                }
            };
        }
    }
});

$filterByHouse.on('change', function () {
    $filterBySection.val('').trigger('change');
    const houseId = $(this).val();
    if (houseId > 0) {
        $filterBySection.removeAttr('disabled');
        initHouseNestedSelects(houseId);
    }
});

function initHouseNestedSelects(houseId) {
    $filterBySection.select2({
        debug: true,
        dropdownParent: $('#filter-by-section-wrap'),
        placeholder: '',
        allowClear: true,
        ajax: {
            type: "GET",
            url: 'sections/get-sections-by-house/' + houseId,
            data: function (params) {
                return {
                    name: params.term || '',
                    page: (params.page - 1) || 0,
                    pageSize: 10
                };
            },
            processResults: function (response) {
                return {
                    results: $.map(response.content, function (section) {
                        return {
                            id: section.id,
                            text: section.name
                        }
                    }),
                    pagination: {
                        more: !response.last
                    }
                };
            }
        }
    });
}

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

$filterByBalance.select2({
    debug: true,
    dropdownParent: $('#dropdownParent'),
    minimumResultsForSearch: -1,
    placeholder: '',
    allowClear: true
});
$filterByBalance.val('').trigger('change');

$filterByNumber.on('input', function () {
    byNumber = this.value;
    delayBeforeSearch();
});
$filterByStatus.on('change', function () {
    byStatus = this.value;
    delayBeforeSearch();
});

$filterByApartment.on('input', function () {
    byApartment = this.value;
    delayBeforeSearch();
});
$filterByHouse.on('change', function () {
    byHouse = this.value;
    delayBeforeSearch();
});
$filterBySection.on('change', function () {
    bySection = this.value;
    delayBeforeSearch();
});

$filterByOwner.on('change', function () {
    byOwner = this.value;
    delayBeforeSearch();
});
$filterByBalance.on('change', function () {
    byBalance = this.value;
    delayBeforeSearch();
});

$('.clear-filters').on('click', function () {
    $($filterByHouse, $filterBySection)
        .val(null).trigger('change');
    $($filterByStatus).select2('enable', false);
    $filterByOwner.val('').trigger('change');
    $filterByBalance.val('').trigger('change');
    $filterByNumber.val('').trigger('input');
    $filterByApartment.val('').trigger('input');
})

function delayBeforeSearch() {
    let keyPause = 400;
    clearTimeout(timer);
    timer = setTimeout(function () {
        getPersonalAccounts(0)
    }, keyPause);
}

$(window).on("load", function () {
    getPersonalAccounts(currentPage);
    getPersonalAccountsStatistic();
})


function getPersonalAccountsStatistic() {
    $.ajax({
        type: 'get',
        url: 'statistic/get-accounts-statistic',
        dataType: 'json',
        success: function (result) {
            console.log(result)
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
            toastr.error(errorMessage)
        }
    })
    // this.href = url;
    // this.download = true;
    // this.target = '_blank';
})

function addParametersToUrl(url) {
    url.searchParams.append('page', currentPage);
    url.searchParams.append('pageSize', tableLength);
    if (byNumber) url.searchParams.append('accountNumber', byNumber);
    if (byStatus) url.searchParams.append('status', byStatus);
    if (byApartment) url.searchParams.append('apartmentNumber', byApartment);
    if (byHouse) url.searchParams.append('house', byHouse);
    if (bySection) url.searchParams.append('section', bySection);
    if (byOwner) url.searchParams.append('owner', byOwner);
    if (byBalance) url.searchParams.append('balance', byBalance);

    return url
}

function getPersonalAccounts(page) {

    blockCardDody();
    let url = new URL('personal-accounts/get-personal-accounts', window.location.origin + window.location.pathname);
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
            toastr.error(errorMessage);
        }
    });
}

function drawTable(result) {
    if (result.content && result.content.length > 0) {
        for (const personalAccount of result.content) {
            let accountNumber = personalAccount.accountNumber.toString().padStart(10, '0000000000');
            accountNumber = accountNumber.substring(0, 5) + '-' + accountNumber.substring(5, 10);
            const status = getAccountStatusLabel(personalAccount.status);
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
            <td colspan="8" class="text-center fw-bold h4">${emptyTableLabel}</td>
           </tr>`).appendTo('tbody');
    }

    drawPaginationElements(result, 'getPersonalAccounts')
    drawPagination(result.totalPages, currentPage, 'getPersonalAccounts');
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
                    setTimeout(() => getPersonalAccounts(currentPage), 400);
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