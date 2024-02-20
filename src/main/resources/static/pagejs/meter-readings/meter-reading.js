$(document).ready(function () {
    initializeSelects();
    if (statusLink.includes("..")) {

    } else {
        let d = new Date();
        $("#number").val(number);
        $("#creationDate").flatpickr({
            locale: "uk",
            dateFormat: "d.m.Y",
            minDate: moment(d).format('DD.MM.YYYY')
        });
    }
});


function initializeSelects() {
    initializeHouseSelect();
    initializeSectionSelect();
    initializeApartmentSelect();
    initializeServiceSelect();
    initializeStatusSelect();
}

function initializeHouseSelect() {
    $('#house').wrap('<div class="position-relative"></div>').select2({
        dropdownParent: $('#house').parent(),
        language: "uk",
        maximumInputLength: 100,
        ajax: {
            type: "get",
            url: houseLink,
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
    $('#section').wrap('<div class="position-relative"></div>').select2({
        dropdownParent: $('#section').parent(),
        language: "uk",
        maximumInputLength: 100,
        ajax: {
            type: "get",
            url: sectionLink,
            data: function (params) {
                return {
                    search: params.term,
                    page: params.page || 1,
                    houseId: $("#house").val(),
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

function initializeApartmentSelect() {
    $('#apartmentId').wrap('<div class="position-relative"></div>').select2({
        dropdownParent: $('#apartmentId').parent(),
        language: "uk",
        allowClear: true,
        maximumInputLength: 100,
        ajax: {
            type: "get",
            url: apartmentLink,
            data: function (params) {
                return {
                    search: params.term,
                    page: params.page || 1,
                    sectionId: $("#section").val(),
                    houseId: $("#house").val()
                };
            },
            processResults: function (response) {
                return {
                    results: $.map(response.content, function (item) {
                        return {
                            text: item.apartmentNumber,
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
    $('#serviceId').wrap('<div class="position-relative"></div>').select2({
        dropdownParent: $('#serviceId').parent(),
        language: "uk",
        maximumInputLength: 100,
        ajax: {
            type: "get",
            url: serviceLink,
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
    $("#status").wrap('<div class="position-relative"></div>').select2({
        language: "uk",
        dropdownParent: $("#status").parent(),
        minimumResultsForSearch: -1,
        ajax: {
            type: "GET",
            url: statusLink,
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

$("#house").on("change", function () {
    $('#section').val(null).trigger('change');
    $('#section').prop('disabled', false);
    $('#apartmentId').val(null).trigger('change');
    $('#apartmentId').prop('disabled', true);
});
$("#section").on("change", function () {
    $('#apartmentId').val(null).trigger('change');
    $('#apartmentId').prop('disabled', false);
});

$("#save-button").on("click", function () {
    blockCardDody();
    clearAllErrorMessage();
    let formData = collectData();
    sendData(formData);
});

function collectData() {
    let formData = new FormData();
    formData.append("creationDate", $("#creationDate").val());
    let status = $("#status").val() == null? '': $("#status").val();
    formData.append("status", status);
    formData.append("readings", $("#readings").val());
    let apartment = $("#apartmentId").val() == null? '': $("#apartmentId").val();
    formData.append("apartmentId", apartment);
    let service = $("#serviceId").val() == null? '': $("#serviceId").val();
    formData.append("serviceId", service);
    return formData;
}

function sendData(formData) {
    $.ajax({
        type: "POST",
        url: window.location.href,
        data: formData,
        contentType: false,
        processData: false,
        success: function (response) {
            window.location.href = response;
        },
        error: function (error) {
            printErrorMessageToField(error);
        }
    });
}