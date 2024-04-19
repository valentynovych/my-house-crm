let tableLength = 5;
let currentPage = 0;

let byNumber = '';
let byDate = '';
let byMasterType = '';
let byDescription = '';
let byApartment = '';
let byApartmentOwner = '';
let byApartmentOwnerPhone = '';
let byMaster = '';
let byStatus = '';
let timer;

const $filterByNumber = $('#filter-by-number');
const $filterByDate = $('#filter-by-date');
const $filterByMasterType = $('#filter-by-master-type');
const $filterByDescription = $('#filter-by-description');
const $filterByApartment = $('#filter-by-apartment');
const $filterByApartmentOwner = $('#filter-by-apartment-owner');
const $filterByApartmentOwnerPhone = $('#filter-by-apartment-owner-phone');
const $filterByMaster = $('#filter-by-master');
const $filterByStatus = $('#filter-by-status');

function getMasterTypeLabel(status) {
    switch (status) {
        case 'ELECTRICIAN':
            return roleElectrician;
        case 'PLUMBER':
            return rolePlumber;
        default:
            return '- - - -'
    }
}

function getMasterRequestStatus(sheetType) {
    switch (sheetType) {
        case "NEW":
            return labelStatusNew;
        case "IN_PROGRESS":
            return labelStatusInProgress
        case "DONE":
            return labelStatusDone
        case "CANCELED":
            return labelStatusCanceled;
        default:
            return '- - - -'
    }
}


const $flatpickrDate = flatpickr($filterByDate, {dateFormat: "d.m.Y"})

$filterByMasterType.select2({
    dropdownParent: $('#filter-by-master-type-wrap'),
    placeholder: '',
    allowClear: true,
    minimumResultsForSearch: -1,
    dropdownCssClass: 'select2-width'
});

$filterByApartmentOwner.select2({
    dropdownParent: $('#filter-by-apartment-owner-wrap'),
    placeholder: '',
    allowClear: true,
    dropdownCssClass: 'select2-width',
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
})

$filterByMaster.select2({
    dropdownParent: $('#filter-by-master-wrap'),
    placeholder: '',
    allowClear: true,
    dropdownCssClass: 'select2-width',
    maximumInputLength: 50,
    ajax: {
        type: "GET",
        url: 'system-settings/staff/get-staff',
        data: function (params) {
            return {
                name: params.term,
                page: (params.page - 1) || 0,
                pageSize: 10
            };
        },
        processResults: function (response) {
            console.log(response)
            return {
                results: $.map(response.content, function (staff) {
                    return {
                        id: staff.id,
                        text: `${staff.firstName} ${staff.lastName}`
                    }
                }),
                pagination: {
                    more: !response.last
                }
            };
        }
    }
});

$filterByStatus.select2({
    dropdownParent: $('#filter-by-status-wrap'),
    dropdownCssClass: 'select2-width',
    minimumResultsForSearch: -1,
    placeholder: '',
    allowClear: true,
});

$filterByNumber.on('input', function () {
    byNumber = this.value;
    delayBeforeSearch();
});
$filterByDate.on('change', function () {
    byDate = this.value;
    delayBeforeSearch();
});
$filterByMasterType.on('change', function () {
    byMasterType = this.value;
    delayBeforeSearch();
});
$filterByDescription.on('change', function () {
    byDescription = this.value;
    delayBeforeSearch();
});
$filterByApartment.on('input', function () {
    byApartment = this.value;
    delayBeforeSearch();
});
$filterByApartmentOwner.on('change', function () {
    byApartmentOwner = this.value;
    delayBeforeSearch();
});
$filterByApartmentOwnerPhone.on('input', function () {
    byApartmentOwnerPhone = this.value;
    delayBeforeSearch();
});
$filterByMaster.on('change', function () {
    byMaster = this.value;
    delayBeforeSearch();
});
$filterByStatus.on('change', function () {
    byStatus = this.value;
    delayBeforeSearch();
});

$('.clear-filters').on('click', function () {
    $filterByNumber.val('').trigger('input');
    $flatpickrDate.clear();
    $filterByMasterType.val('').trigger('change');
    $filterByDescription.val('').trigger('input');
    $filterByApartment.val('').trigger('input');
    $filterByApartmentOwner.val('').trigger('change');
    $filterByApartmentOwnerPhone.val('').trigger('input');
    $filterByMaster.val('').trigger('change');
    $filterByStatus.val('').trigger('change');
})

function delayBeforeSearch() {
    let keyPause = 400;
    clearTimeout(timer);
    timer = setTimeout(function () {
        getMasterRequests(0)
    }, keyPause);
}

$(window).on("load", function () {
    delayBeforeSearch();
})

function decorateAccountNumber(accountNumber) {
    let s = (accountNumber + '').padStart(10, '0000000000');
    return s.substring(0, 5) + '-' + s.substring(5, 10);
}

function addParametersToUrl(url) {
    url.searchParams.append('page', currentPage);
    url.searchParams.append('pageSize', tableLength);
    if (byNumber) url.searchParams.append('number', byNumber);
    if (byDate) url.searchParams.append('visitDate', byDate);
    if (byMasterType) url.searchParams.append('masterType', byMasterType);
    if (byDescription) url.searchParams.append('description', byDescription);
    if (byApartment) url.searchParams.append('apartment', byApartment);
    if (byApartmentOwner) url.searchParams.append('apartmentOwner', byApartmentOwner);
    if (byApartmentOwnerPhone) url.searchParams.append('phone', byApartmentOwnerPhone);
    if (byMaster) url.searchParams.append('master', byMaster);
    if (byStatus) url.searchParams.append('status', byStatus);

    return url
}

function getMasterRequests(page) {
    currentPage = page;
    processedExpenseAmount = 0;
    processedIncomeAmount = 0;

    blockCardDody();
    let url = new URL('master-requests/get-requests', window.location.origin + window.location.pathname);
    url = addParametersToUrl(url);
    $.ajax({
        type: 'get',
        url: url,
        dataType: 'json',
        success: function (result) {
            console.log(result)
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
        for (const request of result.content) {

            const date = new Date(request.visitDate * 1000);
            let toLocaleString = date.toLocaleString('uk-UA');
            const dateString = toLocaleString.substring(0, toLocaleString.length - 3);
            const apartment = request.apartment;
            const masterName = request.master ? `${request.master.lastName} ${request.master.firstName}` : '---';
            const status = getMasterRequestStatus(request.status);
            const apartmentLabel = `${apartment.apartmentNumber}, ${apartment.house.name}`
            const statusBadge = function (statusValue) {
                switch (statusValue) {
                    case 'NEW':
                        return `<span class="badge rounded-pill bg-primary">${status}</span>`;
                    case 'IN_PROGRESS':
                        return `<span class="badge rounded-pill bg-info">${status}</span>`;
                    case 'DONE':
                        return `<span class="badge rounded-pill bg-success">${status}</span>`;
                    case 'CANCELED':
                        return `<span class="badge rounded-pill bg-danger">${status}</span>`;
                }
            }
            const isExpiredRequest = date < new Date() && request.status === 'NEW';

            $(`<tr data-href="master-requests/view-request/${request.id}" class="cursor-pointer  ${isExpiredRequest ? 'bg-label-danger' : ''}">
            <td class="text-center">${(request.id + '').padStart(3, '000')}</td>
            <td>${dateString}</td>
            <td class="text-center">${getMasterTypeLabel(request.masterType)}</td>
            <td>${request.description}</td>
            <td>${apartmentLabel}</td>
            <td>${apartment.owner.fullName}</td>
            <td class="text-center">${request.apartmentOwnerPhone}</td>
            <td>${masterName}</td>
            <td class="text-center">${statusBadge(request.status)}</td>
            <td class="text-center">
              <div class="dropdown">
               <button type="button" class="btn p-0 dropdown-toggle hide-arrow"
                       data-bs-toggle="dropdown" data-bs-offset="-50,10">
                <i class="ti ti-dots-vertical"></i>
               </button>
                 <div class="dropdown-menu">
                   <a class="dropdown-item" href="master-requests/edit-request/${request.id}">
                       <i class="ti ti-pencil me-1"></i>${buttonLabelEdit}
                   </a>
                   <button type="button" class="dropdown-item btn justify-content-start" 
                        data-bs-toggle="modal" data-bs-target="#modalToDelete" onclick="addDeleteEvent(${request.id})">
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
            <td colspan="10" class="text-center fw-bold h4">${emptyTableLabel}</td>
           </tr>`).appendTo('tbody');
    }

    drawPaginationElements(result, 'getMasterRequests')
    drawPagination(result.totalPages, currentPage, 'getMasterRequests');
}

function addListenerToRow() {
    $('tr[data-href] td:not(:last-child)').on('click', function () {
        window.location = $(this).parent().attr('data-href');
    })
}

function addDeleteEvent(requestId) {
    $('.submit-delete').on('click', function () {
        $.ajax({
            url: 'master-requests/delete/' + requestId,
            type: 'delete',
            success: function (response) {
                $('.close-modal').click();
                toastr.success(successMessageOnDelete);
                setTimeout(() => getMasterRequests(currentPage), 500);
            }, error: function (error) {
                console.log(error);
                $('.close-modal').click();
                if (error.status === 423) {
                    toastr.error(errorMessageOnDelete);
                } else {
                    toastr.error(errorMessage);
                }
            }
        });
    });
}

function applySearchParameters() {

    applyPaymentType();
    applyPersonalAccount();

    function applyPaymentType() {
        const paymentType = findGetParameter('sheetType');
        if (paymentType) {
            byPaymentType = paymentType;
            $filterByPaymentType.val(byPaymentType).trigger('change');
        }
    }

    function applyPersonalAccount() {
        const personalAccountId = findGetParameter('personalAccount');
        if (personalAccountId) {
            byPersonalAccount = personalAccountId;
            $.ajax({
                url: 'personal-accounts/get-account/' + personalAccountId,
                type: 'get',
                success: function (response) {
                    const personalAccountData = {
                        id: response.id,
                        text: decorateAccountNumber(response.accountNumber)
                    };
                    const newOption = new Option(personalAccountData.text, personalAccountData.id, true, true);
                    $filterByPersonalAccount.append(newOption);

                },
                error: function (error) {
                    toastr.error(errorMessage);
                },
            })
        }
    }
}

function findGetParameter(parameterName) {
    let result = null,
        tmp = [];
    location.search
        .substr(1)
        .split("&")
        .forEach(function (item) {
            tmp = item.split("=");
            if (tmp[0] === parameterName) result = decodeURIComponent(tmp[1]);
        });
    return result;
}

function clearTableLine() {
    $("tbody").find("tr").each(function () {
        this.remove();
    });
}