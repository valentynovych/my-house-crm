let timer;
let entryId;
let tableLength = 5;
let request = {
    page: 0,
    pageSize: tableLength,
    number:'',
    status: '',
    creationDate: '',
    houseId: '',
    sectionId: '',
    apartment: '',
    serviceId: ''
};
$(document).ready(function () {
    getApartmentReadings(0);
    initializeFlatPickr();
    initializeSelects();
});
function initializeFlatPickr() {
    $("#filter-by-creationDate").flatpickr({
        locale: "uk",
        dateFormat: "d.m.Y"
    });
}

function initializeSelects() {
    initializeHouseSelect();
    initializeSectionSelect();
    initializeServiceSelect();
    initializeStatusSelect();
}
function initializeHouseSelect() {
    $('#filter-by-house').select2({
        dropdownParent: $('#dropdownParent'),
        language: "uk",
        placeholder:"",
        allowClear: true,
        maximumInputLength: 100,
        ajax: {
            type: "get",
            url: "../get-houses",
            data: function (params) {
                return {
                    search: params.term,
                    page: params.page || 1,
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
function initializeSectionSelect() {
    $('#filter-by-section').select2({
        dropdownParent: $('#dropdownParent'),
        language: "uk",
        placeholder:"",
        allowClear: true,
        maximumInputLength: 100,
        ajax: {
            type: "get",
            url: "../get-sections",
            data: function (params) {
                return {
                    search: params.term,
                    page: params.page || 1,
                    houseId: $("#filter-by-house").val(),
                    apartmentNumber: $("#filter-by-apartment").val()
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
function initializeServiceSelect() {
    $('#filter-by-service').select2({
        dropdownParent: $('#dropdownParent'),
        language: "uk",
        placeholder:"",
        allowClear: true,
        maximumInputLength: 100,
        ajax: {
            type: "get",
            url: "../get-services",
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
    $("#filter-by-status").select2({
        language: "uk",
        dropdownParent: $("#dropdownParent"),
        minimumResultsForSearch: -1,
        ajax: {
            type: "GET",
            url: "../get-statuses",
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

function getStatus(status) {
    switch (status) {
        case 'NEW':
            return newStatus;
        case 'INCLUDED':
            return includedStatus;
        case 'INCLUDED_AND_PAID':
            return includedPaidStatus;
        case 'ZERO':
            return zeroStatus;
    }
}
function getApartmentReadings(currentPage) {
    blockCardDody();
    request.page = currentPage;
    request.pageSize = tableLength;
    let url = window.location.pathname;
    let id = url.substring(url.lastIndexOf('/') + 1);
    $.ajax({
        type: "GET",
        url: "../get-by-apartment/"+id,
        data: request,
        success: function (response) {
            if(response.content[0] !== undefined) {
                $(".reading").text(reading + ", " + apartment + "." + response.content[0].apartmentNumber)
            }
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
        for (const reading of response.content) {
            var parts = reading.creationDate.split('.');
            let date = new Date(parts[2],parts[1]-1,parts[0]).toLocaleString(dateLocale,{month:'long', year:'numeric'});
            $("tbody")
                .append(
                    `<tr class="tr text-nowrap">
                    <td>${reading.number}</td>
                    <td>${getStatusSpan(reading.status)}</td>
                    <td>${reading.creationDate}</td>
                    <td>${date}</td>
                    <td>${reading.houseName}</td>
                    <td>${reading.sectionName}</td>
                    <td>${reading.apartmentNumber}</td>
                    <td>${reading.serviceName}</td>
                    <td>${reading.readings}</td>
                    <td>${reading.measurementName}</td>
                    <td>
                    <div class="dropdown">
                        <button type="button" class="btn p-0 dropdown-toggle hide-arrow" data-bs-toggle="dropdown">
                            <i class="ti ti-dots-vertical"></i>
                        </button>
                        <div class="dropdown-menu">
                            <a class="dropdown-item" href="../edit/${reading.id}">
                                <i class="ti ti-pencil me-1"></i>${buttonLabelEdit}
                            </a>
                            <button type="button" class="dropdown-item btn justify-content-start" onclick="openDeleteModal(${reading.id})">
                                <i class="ti ti-trash me-1"></i>${buttonLabelDelete}
                            </button>
                        </div>
                    </div>
                    </td> </tr>`);
        }
        if (response.totalPages > 0) {
            const page = response.pageable.pageNumber;
            drawPaginationElements(response, "getApartmentReadings");
            drawPagination(response.totalPages, page, 'getApartmentReadings');
        }
    }
}

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
        url: "../delete/"+entryId,
        success: function () {
            $('#deleteModal').modal('hide');
            toastr.success(deleteSuccessful);
            getApartmentReadings(0)
        },
        error: function () {
            $('#deleteModal').modal('hide');
            toastr.error(errorMessage);
        }
    });
}

function getStatusSpan(status) {
    switch (status) {
        case 'NEW':
            return '<span class="badge bg-label-info">'+ newStatus +'</span>';
        case 'INCLUDED':
            return '<span class="badge bg-label-success">'+ includedStatus +'</span>';
        case 'INCLUDED_AND_PAID':
            return '<span class="badge bg-label-primary">'+ includedPaidStatus +'</span>';
        case 'ZERO':
            return '<span class="badge bg-label-warning">'+ zeroStatus +'</span>';
    }
}
$("#filter-by-number").on("input", function () {
    request.number =  $(this).val();
    searchAfterDelay();
});
$('#filter-by-status').on("change", function () {
    request.status = $(this).val();
    searchAfterDelay();
});
$("#filter-by-creationDate").on("change", function () {
    request.creationDate =  $(this).val();
    searchAfterDelay();
});
$('#filter-by-house').on("change", function () {
    request.houseId = $(this).val();
    $('#filter-by-section').val(null).trigger('change');
    $('#filter-by-section').prop('disabled', false);
    $('#filter-by-apartment').val("");
    $('#filter-by-apartment').prop('disabled', false);
    searchAfterDelay();
});
$('#filter-by-section').on("change", function () {
    request.sectionId = $(this).val();
    $('#filter-by-apartment').val("");
    $('#filter-by-apartment').prop('disabled', false);
    searchAfterDelay();
});
$('#filter-by-service').on("change", function () {
    request.serviceId = $(this).val();
    searchAfterDelay();
});
$("#filter-by-apartment").on("input", function () {
    $(this).val($(this).val().replace(/[^0-9]/g, ''));
    request.apartment =  $(this).val();
    searchAfterDelay();
});
function searchAfterDelay() {
    clearTimeout(timer);
    timer = setTimeout(function() {
        getApartmentReadings(0);
    }, 1000);
}

$('.clear-filters').on('click', function () {
    $('#filter-by-house, #filter-by-section, #filter-by-service, #filter-by-status, #filter-by-creationDate')
        .val('').trigger('change');
    $('#filter-by-apartment, #filter-by-number').val('').trigger('input');
    $('#filter-by-apartment').prop('disabled', true);
    $('#filter-by-section').prop('disabled', true);
})