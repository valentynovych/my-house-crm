let tableLength = 5;
let timer;
let entryId;
let request = {
    page: 0,
    pageSize: tableLength,
    ownerId: "",
    fullName: "",
    phoneNumber: "",
    email: "",
    houseId: "",
    apartment: "",
    creationDate: "",
    status: "",
    hasDebt: ""
};


$(document).ready(function () {
    getOwners(0);
    initializeHouseSelect();
    initializeStatusSelect();
    initializeDebtSelect();
    initializeFlatPickr();
});

function initializeHouseSelect() {
    $('#filter-by-house').select2({
        dropdownParent: $('#dropdownParent'),
        maximumInputLength: 100,
        placeholder:"",
        allowClear: true,
        ajax: {
            type: "get",
            url: "owners/get-houses",
            data: function (params) {
                return {
                    search: params.term,
                    page: params.page || 1
                };
            },
            processResults: function (response) {
                return {
                    results: $.map(response.content, function (item) {
                        return {
                            text: item.name,
                            id: item.id
                        }
                    }),
                    pagination: {
                        more: (response.pageable.pageNumber + 1) < response.totalPages
                    }
                };
            }

        }
    });
}
function initializeStatusSelect() {
    $('#filter-by-status').select2({
        dropdownParent: $('#dropdownParent'),
        minimumResultsForSearch: -1,
        placeholder:"",
        allowClear: true,
        ajax: {
            type: "GET",
            url: "owners/get-statuses",
            processResults: function (response) {
                return {
                    results: $.map(response, function (item) {
                        return {
                            text: getStatus(item),
                            id: item
                        }
                    })
                };
            }

        }
    });
}
function initializeDebtSelect() {
    $('#filter-by-debt').select2({
        minimumResultsForSearch: -1,
        dropdownParent: $('#dropdownParent'),
        placeholder: " ",
        allowClear: true
    });
}
function getStatus(status) {
    switch (status) {
        case 'NEW':
            return newStatus;
        case 'ACTIVE':
            return activeStatus;
        case 'DISABLED':
            return disabledStatus;
    }
}
function initializeFlatPickr() {
    $("#filter-by-creation-date").flatpickr({
        dateFormat: "d.m.Y"
    });
}

$("#filter-by-id").on("input", function () {
    request.ownerId = $(this).val();
    searchAfterDelay();
});
$("#filter-by-fullName").on("input", function () {
    request.fullName = $(this).val();
    searchAfterDelay();
});
$("#filter-by-phoneNumber").on("input", function () {
    request.phoneNumber = $(this).val();
    searchAfterDelay();
});
$("#filter-by-email").on("input", function () {
    request.email = $(this).val();
    searchAfterDelay();
});
$("#filter-by-house").on("change", function () {
    request.houseId = $(this).val();
    searchAfterDelay();
});
$("#filter-by-apartment").on("input", function () {
    request.apartment = $(this).val();
    searchAfterDelay();
});
$("#filter-by-creation-date").on("change", function () {
    request.creationDate = $(this).val();
    searchAfterDelay();
});
$("#filter-by-debt").on("change", function () {
    request.hasDebt = $(this).val();
    searchAfterDelay();
});
$("#filter-by-status").on("change", function () {
    request.status = $(this).val();
    searchAfterDelay();
});

function searchAfterDelay() {
    clearTimeout(timer);
    timer = setTimeout(function() {
        getOwners(0);
    }, 800);
}

function getOwners(currentPage) {
    blockCardDody();
    request.page = currentPage;
    request.pageSize = tableLength;
    $.ajax({
        type: "GET",
        url: "owners/getOwners",
        data: request,
        success: function (response) {
            $("tbody").children().remove();
            $(".card-footer").children().remove();
            drawTable(response);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}

function drawTable(response) {
    if (response.numberOfElements == 0) {
        let tdCount = $("td").length;
        $("tbody").append(`<tr class="tr"><td colspan="${tdCount}" class="text-center">${dataNotFound}</td>></tr>`);
    } else {
        for (const owner of response.content) {
            $("tbody")
                .append(
                    `<tr class="tr text-nowrap" data-href="owners/view-owner/${owner.id}">
                    <td>${owner.ownerId}</td>
                    <td>${owner.fullName}</td>
                    <td>${owner.phoneNumber}</td>
                    <td>${owner.email}</td>
                    <td>${getHousesP(owner.houseApartmentResponses)}</td>
                    <td>${getApartmentsP(owner.houseApartmentResponses)}</td>
                    <td>${owner.creationDate}</td>
                    <td>${getStatusSpan(owner.status)}</td>
                    <td>${getHasDebt(owner.hasDebt)}</td>
                    <td>
                    <div class="dropdown">
                        <button type="button" class="btn p-0 dropdown-toggle hide-arrow" data-bs-toggle="dropdown">
                            <i class="ti ti-dots-vertical"></i>
                        </button>
                        <div class="dropdown-menu">
                            <button type="button" 
                            class="dropdown-item btn justify-content-start" 
                            onclick="sendOwnerActivation(${owner.id})"
                            ${setDisabled(owner.status)}>
                                <i class="ti ti-mail me-1"></i>${sendActivation}
                            </button>
                            <a class="dropdown-item" href="owners/edit/${owner.id}">
                                <i class="ti ti-pencil me-1"></i>${buttonLabelEdit}
                            </a>
                            <button type="button" class="dropdown-item btn justify-content-start" onclick="openDeleteModal(${owner.id})">
                                <i class="ti ti-trash me-1"></i>${buttonLabelDelete}
                            </button>
                        </div>
                    </div>
                    </td> </tr>`);
        }
        if (response.totalPages > 0) {
            const page = response.pageable.pageNumber;
            drawPaginationElements(response, "getOwners");
            drawPagination(response.totalPages, page, 'getOwners');
        }
        addListenerToRow();
    }
}
function getHousesP(houseApartments) {
    let houses = "";
    for(let house of houseApartments){
        houses += "<p>"+house.house+"</p>"
    }
    return houses;
}
function getApartmentsP(houseApartments) {
    let apartments = "";
    for(let apartment of houseApartments){
        apartments += "<p>"+apartment.apartment+"</p>"
    }
    return apartments;
}
function getHasDebt(hasDebt) {
    return hasDebt? haveDebt: notHaveDebt;
}
function getStatusSpan(status){
    switch (status) {
        case 'NEW':
            return '<span class="badge bg-label-info">'+ newStatus +'</span>';
        case 'ACTIVE':
            return '<span class="badge bg-label-success">'+ activeStatus +'</span>';
        case 'DISABLED':
            return '<span class="badge bg-label-danger">'+ disabledStatus +'</span>';
    }
}
function sendOwnerActivation(ownerId) {
    $.ajax({
        type: "POST",
        url: "owners/send-activation/"+ownerId,
        success: function () {
            toastr.success(sentMessage);
        },
        error: function (errorResponse) {
            toastr.error(errorMessage);
        }
    });
}
function setDisabled(status) {
    switch (status) {
        case 'NEW':
            return '';
        case 'ACTIVE':
            return 'disabled';
        case 'DISABLED':
            return 'disabled';
    }
}

$('.clear-filters').on('click', function () {
    $('#filter-by-house, #filter-by-creation-date, #filter-by-debt, #filter-by-status')
        .val('').trigger('change');
    $('#filter-by-id, #filter-by-fullName, #filter-by-phoneNumber, #filter-by-email, #filter-by-apartment')
        .val('').trigger('input');
})
function openDeleteModal(id) {
    if($("#deleteModal").length === 0) {
        $("div.card").append(
            `<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="exampleModalLabel"
             aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <h4>${deleteModalText}</h4>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-label-secondary close-modal" data-bs-dismiss="modal">
                        ${modalCloseButton}
                        </button>
                        <button type="button" class="btn btn-danger" id="delete-button" onclick="deleteEntry()">
                            ${modalDeleteButton}
                        </button>
                    </div>
                </div>
            </div>
        </div>`
        )
    }
    $('#deleteModal').modal('show');
    entryId = id;
}
function deleteEntry() {
    $("#delete-button").prop('disabled', true);
    $.ajax({
        type: "GET",
        url: "owners/delete/"+entryId,
        success: function () {
            $('#deleteModal').modal('hide');
            toastr.success(deleteSuccessful);
            getOwners(0)
        },
        error: function (errorResponse) {
            $('#deleteModal').modal('hide');
            if (errorResponse.status === 409) {
                toastr.warning(deleteErrorMessage);
            } else {
                toastr.error(errorMessage);
            }
        }
    });
    $("#delete-button").prop('disabled', true);
}
function addListenerToRow() {
    $('tr[data-href] td:not(:last-child)').on('click', function () {
        window.location = $(this).parent().attr('data-href');
    })
}
