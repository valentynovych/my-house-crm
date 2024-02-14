let tableLength = 5;
let currentPage = 0;
let byNumber = '';
let byHouse = '';
let bySection = '';
let byFloor = '';
let byOwner = '';
let byBalance = '';
let timer;

let $filterByNumber = $('#filter-by-number');
let $filterByHouse = $('#filter-by-house');
let $filterBySection = $('#filter-by-section');
let $filterByFloor = $('#filter-by-floor');
let $filterByOwner = $('#filter-by-owner');
let $filterByBalance = $('#filter-by-balance');

$filterByHouse.select2({
    dropdownParent: $filterByHouse.parent(),
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
    $filterByFloor.val('').trigger('change');
    const houseId = $(this).val();
    if (houseId > 0) {
        $filterBySection.removeAttr('disabled');
        $filterByFloor.removeAttr("disabled")
        initHouseNestedSelects(houseId);
    }
});

function initHouseNestedSelects(houseId) {
    $filterBySection.select2({
        debug: true,
        dropdownParent: $('#dropdownParent'),
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

    $filterByFloor.select2({
        debug: true,
        dropdownParent: $('#dropdownParent'),
        placeholder: '',
        allowClear: true,
        ajax: {
            type: "GET",
            url: 'floors/get-floors-by-house/' + houseId,
            data: function (params) {
                return {
                    name: params.term || '',
                    page: (params.page - 1) || 0,
                    pageSize: 10
                };
            },
            processResults: function (response) {
                return {
                    results: $.map(response.content, function (floor) {
                        return {
                            id: floor.id,
                            text: floor.name
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
    dropdownParent: $('#dropdownParent'),
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
$filterByHouse.on('change', function () {
    byHouse = this.value;
    delayBeforeSearch();
});
$filterBySection.on('change', function () {
    bySection = this.value;
    delayBeforeSearch();
});
$filterByFloor.on('change', function () {
    byFloor = this.value;
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
    $($filterByHouse, $filterBySection, $filterByFloor)
        .val(null).trigger('change');
    $($filterBySection).select2('enable', false);
    $($filterByFloor).select2('enable', false);
    $filterByOwner.val('').trigger('change');
    $filterByBalance.val('').trigger('change');
    $filterByNumber.val('').trigger('input');
})

function delayBeforeSearch() {
    let keyPause = 400;
    clearTimeout(timer);
    timer = setTimeout(function () {
        getApartments(0)
    }, keyPause);
}

$(window).on("load", function () {
    getApartments(currentPage);
})

function getApartments(page) {

    blockCardDody();
    let url = new URL('apartments/get-apartments', window.location.origin + window.location.pathname);
    url.searchParams.append('page', page);
    url.searchParams.append('pageSize', tableLength);
    if (byNumber) url.searchParams.append('apartmentNumber', byNumber);
    if (byHouse) url.searchParams.append('house', byHouse);
    if (bySection) url.searchParams.append('section', bySection);
    if (byFloor) url.searchParams.append('floor', byFloor);
    if (byOwner) url.searchParams.append('owner', byOwner);
    if (byBalance) url.searchParams.append('balance', byBalance);

    $.ajax({
        type: 'get',
        url: url,
        dataType: 'json',
        success: function (result) {
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
    const page = result.pageable.pageNumber;
    let iter = 0;
    if (result.content && result.content.length > 0) {
        for (const apartment of result.content) {
            const apartmentNumber = apartment.apartmentNumber.toString().padStart(5, '00000')
            $(`<tr data-href="apartments/view-apartment/${apartment.id} + " class="cursor-pointer">
            <td>${apartmentNumber}</td>
            <td>${apartment.house.name}</td>
            <td>${apartment.section.name}</td>
            <td>${apartment.floor.name}</td>
            <td>${apartment.owner.fullName}</td>
            <td>${apartment.balance}</td>
            <td>
              <div class="dropdown">
               <button type="button" class="btn p-0 dropdown-toggle hide-arrow"
                       data-bs-toggle="dropdown">
                <i class="ti ti-dots-vertical"></i>
               </button>
                 <div class="dropdown-menu">
                   <a class="dropdown-item" href="apartments/edit-apartment/${apartment.id}">
                       <i class="ti ti-pencil me-1"></i>${buttonLabelEdit}
                   </a>
                   <button type="button" class="dropdown-item btn justify-content-start" 
                        data-bs-toggle="modal" data-bs-target="#modalToDelete" onclick="addDeleteEvent(${apartment.id})">
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
            <td colspan="7" class="text-center fw-bold h4">${emptyTableLabel}</td>
           </tr>`).appendTo('tbody');
    }

    drawPaginationElements(result, 'getStaff')
    drawPagination(result.totalPages, page, 'getStaff');

}

function addListenerToRow() {
    $('tr[data-href] td:not(:last-child)').on('click', function () {
        window.location = $(this).parent().attr('data-href');
    })
}

function addDeleteEvent(staffId) {
    $('.submit-delete').on('click', function () {
        if (staffId && staffId > 0) {

            $.ajax({
                type: 'delete',
                url: 'staff/delete/' + staffId,
                success: function () {
                    $('.close-modal').click()
                    toastr.success(successMessageOnDelete)
                    setTimeout(() => getApartments(currentPage), 400);
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