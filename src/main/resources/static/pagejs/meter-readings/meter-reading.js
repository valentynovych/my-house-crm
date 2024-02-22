let defaultReading;
$(document).ready(function () {
    initializeSelects();
    if (statusLink.includes("..")) {
        getReading();
    } else {
        $("#breadCrumb").text(newReading);
        $("#pageTitle").text(newReading);
        let d = new Date();
        setNumber();
        $("#creationDate").flatpickr({
            locale: "uk",
            dateFormat: "d.m.Y",
            minDate: moment(d).format('DD.MM.YYYY')
        });
    }
});

function setNumber() {
    $.ajax({
        type: "GET",
        url: "get-number",
        success: function (response) {
            console.log(response);
            $("#number").val(response);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}

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
        placeholder: chooseHouse,
        ajax: {
            type: "get",
            url: houseLink,
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

function initializeSectionSelect() {
    $('#section').wrap('<div class="position-relative"></div>').select2({
        dropdownParent: $('#section').parent(),
        language: "uk",
        maximumInputLength: 100,
        placeholder: chooseSection,
        ajax: {
            type: "get",
            url: sectionLink,
            data: function (params) {
                return {
                    search: params.term,
                    page: params.page || 1,
                    houseId: $("#house").val()
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
        maximumInputLength: 100,
        placeholder: chooseApartment,
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
        placeholder: chooseService,
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
        placeholder: chooseStatus,
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

function getReading() {
    blockCardDody();
    let url = window.location.pathname;
    let id = url.substring(url.lastIndexOf('/') + 1);
    $.ajax({
        type: "GET",
        url: "../get-reading/"+id,
        success: function (response) {
            console.log(response);
            defaultReading = response;
            setFields(response);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}
function setFields(response) {
    $("#breadCrumb").text(editReading);
    $("#pageTitle").text(editReading);
    let d = new Date();
    $("#creationDate").flatpickr({
        locale: "uk",
        defaultDate: response.creationDate,
        dateFormat: "d.m.Y",
        minDate: response.creationDate
    });
    let statusOption = new Option(getStatus(response.status), response.status, true, true);
    $('#status').append(statusOption).trigger('change');
    let serviceOption = new Option(response.serviceNameResponse.name, response.serviceNameResponse.id, true, true);
    $('#serviceId').append(serviceOption).trigger('change');
    let houseOption = new Option(response.houseNameResponse.name, response.houseNameResponse.id, true, true);
    $('#house').append(houseOption).trigger('change');
    let sectionOption = new Option(response.sectionNameResponse.name, response.sectionNameResponse.id, true, true);
    $('#section').append(sectionOption).trigger('change');
    let apartmentOption = new Option(response.apartmentNumberResponse.apartmentNumber, response.apartmentNumberResponse.id, true, true);
    $('#apartmentId').append(apartmentOption).trigger('change');
    $("#number").val(response.number);
    $("#readings").val(response.readings);
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
            toastr.success(successMessage);
            window.location.href = response;
        },
        error: function (error) {
            printErrorMessageToField(error);
        }
    });
}

$("#cancel-button").on("click", function () {
    blockBy("#form");
    clearFields();
    if(defaultReading !== undefined){
        setFields(defaultReading);
    }
    unblockBy("#form");
});
function clearFields() {
    $("#form").find('input:text, #readings').val('');
    $("#form").find('select').val(null).trigger('change');
}

$("#save-add-button").on("click", function () {
    blockCardDody();
    clearAllErrorMessage();
    let formData = collectData();
    formData.append("notReturn",true);
    sendData(formData);
});



