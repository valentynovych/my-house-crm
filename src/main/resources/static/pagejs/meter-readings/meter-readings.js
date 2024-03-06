let timer;
let tableLength = 5;
let request = {
    page: 0,
    pageSize: tableLength,
    houseId: '',
    sectionId: '',
    apartment: '',
    serviceId: ''
};
$(document).ready(function () {
    getReadings(0);
    initializeSelects();
});

function initializeSelects() {
    initializeHouseSelect();
    initializeSectionSelect();
    initializeServiceSelect();
}
function initializeHouseSelect() {
    $('#filter-by-house').select2({
        dropdownParent: $('#dropdownParent'),
        placeholder:"",
        allowClear: true,
        maximumInputLength: 100,
        ajax: {
            type: "get",
            url: "meter-readings/get-houses",
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
        placeholder:"",
        allowClear: true,
        maximumInputLength: 100,
        ajax: {
            type: "get",
            url: "meter-readings/get-sections",
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
        placeholder:"",
        allowClear: true,
        maximumInputLength: 100,
        ajax: {
            type: "get",
            url: "meter-readings/get-services",
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

function getReadings(currentPage) {
    blockCardDody();
    request.page = currentPage;
    request.pageSize = tableLength;
    $.ajax({
        type: "GET",
        url: "meter-readings/get",
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
        for (const reading of response.content) {
            $("tbody")
                .append(
                    `<tr class="tr text-nowrap">
                    <td>${reading.houseName}</td>
                    <td>${reading.sectionName}</td>
                    <td>${reading.apartmentName}</td>
                    <td>${reading.serviceName}</td>
                    <td>${reading.readings}</td>
                    <td>${reading.measurementName}</td>
                    <td>
                    <div class="dropdown">
                        <button type="button" class="btn p-0 dropdown-toggle hide-arrow" data-bs-toggle="dropdown">
                            <i class="ti ti-dots-vertical"></i>
                        </button>
                        <div class="dropdown-menu">
                            <a class="dropdown-item" href="meter-readings/apartment/${reading.apartmentId}">
                                <i class="ti ti-eye me-1"></i>${history}
                            </a>
                            <a class="dropdown-item" href="meter-readings/edit/${reading.id}">
                                <i class="ti ti-pencil me-1"></i>${buttonLabelEdit}
                            </a>
                    </td> </tr>`);
        }
        if (response.totalPages > 0) {
            const page = response.pageable.pageNumber;
            drawPaginationElements(response, "getReadings");
            drawPagination(response.totalPages, page, 'getReadings');
        }
    }
}

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
        getReadings(0);
    }, 1000);
}

$('.clear-filters').on('click', function () {
    $('#filter-by-house, #filter-by-section, #filter-by-service')
        .val('').trigger('change');
    $('#filter-by-apartment')
        .val('').trigger('input');
    $('#filter-by-apartment').prop('disabled', true);
    $('#filter-by-section').prop('disabled', true);
})