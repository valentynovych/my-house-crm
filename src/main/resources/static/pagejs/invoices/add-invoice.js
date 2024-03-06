let i = 0;
let tableLength = 5;
let meterReadings;
let tariffServices;
$(document).ready(function () {
    initializeSelects();
    setNumber();
    $("#creationDate").flatpickr({
        dateFormat: "d.m.Y",
        defaultDate: new Date()
    });
    getReadings(0);
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
    initializeStatusSelect();
}

function initializeHouseSelect() {
    $('#house').wrap('<div class="position-relative"></div>').select2({
        dropdownParent: $('#house').parent(),
        maximumInputLength: 100,
        placeholder: chooseHouse,
        ajax: {
            type: "get",
            url: "get-houses",
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
        maximumInputLength: 100,
        placeholder: chooseSection,
        ajax: {
            type: "get",
            url: "get-sections",
            data: function (params) {
                return {
                    search: params.term,
                    page: params.page || 1,
                    houseId: $("#house").val(),
                    apartmentId: $('#apartmentId').val()
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
        maximumInputLength: 100,
        placeholder: chooseApartment,
        ajax: {
            type: "get",
            url: "get-apartments",
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

function initializeStatusSelect() {
    $("#status").wrap('<div class="position-relative"></div>').select2({
        dropdownParent: $("#status").parent(),
        minimumResultsForSearch: -1,
        placeholder: chooseStatus,
        ajax: {
            type: "GET",
            url: "get-statuses",
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

$("#house").on("change", function () {
    $('#section').val(null).trigger('change');
    $('#section').prop('disabled', false);
    $('#apartmentId').val(null).trigger('change');
    $('#apartmentId').prop('disabled', false);
});
$("#section").on("change", function () {
    $('#apartmentId').val(null).trigger('change');
    $('#apartmentId').prop('disabled', false);
});
$("#apartmentId").on("change", function () {
    if ($(this).val() != null) {
        $.ajax({
            type: "GET",
            url: "get-owner",
            data: {
                apartmentId: $(this).val()
            },
            success: function (response) {
                setOwnerFields(response);
            },
            error: function () {
                toastr.error(errorMessage);
            }
        });
        getReadings(0);
    } else {
        $("#owner").text("");
        $("#phone-number").text("");
        $("#personalAccount").val("");
        $('#tariff').val(null).trigger('change');
    }
});

function setOwnerFields(response) {
    $("#owner").text(response.ownerFullName);
    $("#phone-number").text(response.ownerPhoneNumber);
    let number = "";
    for (let j = 0; j < 10 - response.accountNumber.toString().length; j++) {
        number += "0";
    }
    number += response.accountNumber;
    let accountNumber = number.substring(0, 5) + "-" + number.substring(5, 10)
    $("#personalAccount").val(accountNumber);
    let houseOption = new Option(response.tariffName, response.tariffId, true, true);
    $('#tariff').append(houseOption).trigger('change');
}

function getStatus(status) {
    switch (status) {
        case 'PAID':
            return paidStatus;
        case 'UNPAID':
            return unpaidStatus;
        case 'PARTLY_PAID':
            return partlyPaidStatus;
    }
}

$("#add-service").on("click", function () {
    if ($("#tariff").val() == null) {
        toastr.warning(chooseApartment);
    } else {
        $("#service-table tr:last").remove();
        setRowInServiceTable();
        initializeServiceSelects();
        addListenerToSelects();
        appendTotal();
        calculateTotal();
        i++;
    }
});

function setRowInServiceTable() {
    $("#service-table").append(
        `<tr>
            <td><input class="form-check-input" name="checks" type="checkbox"></td>
            <td class="px-2" style="min-width: 13rem">
                <select class="form-select select2" id="service${i}" 
                name="itemRequests[${i}].serviceId"
            </td>
            <td class="px-2">
                <input type="text" class="form-control quantity"
                oninput="calculateCost(this)" placeholder="0.00" 
                name="itemRequests[${i}].amount">
            </td>
            <td class="px-2">
                <input type="text" class="form-control unit"  disabled>
            </td>
            <td class="px-2">
                <input type="text" class="form-control per-unit" 
                oninput="calculateCost(this)" 
                placeholder="0.00" 
                name="itemRequests[${i}].pricePerUnit">
            </td>
            <td class="px-2">
                <input type="text" class="form-control cost" disabled 
                oninput="calculateTotal(this)" name="itemRequests[${i}].cost">
            </td>
            <td class="px-2">
                <button type="button" class="btn btn-icon btn-label-danger" 
                onclick="openDeleteModal(this)">
                    <span class="ti ti-trash"></span>
                </button>
            </td>
        </tr>`
    );
}
function addListenerToSelects() {
    $(".select2").on("change", function () {
        $(this).parent().parent().find(".quantity").val("").trigger("input");
        setUnit(this);
        setPricePerUnit(this);
    });
}
function setUnit(select) {
    $.ajax({
        type: "GET",
        url: "get-unit-name",
        data: {
            serviceId: $(select).val()
        },
        success: function (response) {
            let unitInput = $(select).parent().parent().find(".unit");
            unitInput.val(response.name);
            unitInput.prop('disabled', true);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}
function setPricePerUnit(select) {
    let count = 0;
    for (tariffService of tariffServices) {
        if ($(select).val() == tariffService.serviceId) {
            let perUnitInput = $(select).parent().parent().find(".per-unit");
            perUnitInput.val(tariffService.servicePrice);
            perUnitInput.trigger("input");
            perUnitInput.prop('disabled', true);
            count++;
        }
    }
    if (count === 0) {
        let perUnitInput = $(select).parent().parent().find(".per-unit");
        perUnitInput.val("");
        perUnitInput.trigger("input");
        perUnitInput.prop('disabled', false);
    }
}

function calculateCost(input) {
    let perUnitInput = $(input).parent().parent().find(".per-unit");
    let quantityInput = $(input).parent().parent().find(".quantity");
    let costInput = $(input).parent().parent().find(".cost");
    costInput.val((perUnitInput.val() * quantityInput.val()).toFixed(2));
    costInput.trigger("input");
}
function calculateTotal() {
    let sum = 0;
    $("#service-table").find(".cost").each(function () {
        sum += Number($(this).val());
    });
    $("#total").text(total+": ");
    $("#totalPrice").text(sum).trigger("change");
}
let deleteButton;
function openDeleteModal(button) {
    if($("#deleteModal").length === 0) {
        $("#dropdownParent").append(
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
                        <button type="button" class="btn btn-danger" id="delete-button" onclick="deleteRow()">
                            ${modalDeleteButton}
                        </button>
                    </div>
                </div>
            </div>
        </div>`
        )
    }
    $('#deleteModal').modal('show');
    deleteButton = button;
}
function deleteRow() {
    $(deleteButton).parent().parent().remove();
    calculateTotal();
    $('#deleteModal').modal('hide');
}

function initializeServiceSelects() {
    $("#service" + i).select2({
        dropdownParent: $('#dropdownParent'),
        maximumInputLength: 100,
        ajax: {
            type: "get",
            url: "get-services",
            data: function (params) {
                return {
                    search: params.term,
                    page: params.page || 1
                };
            },
            processResults: function (response) {
                let values = [];
                $("#service-table").find("select").each(function () {
                    values.push(Number($(this).val()));
                });
                console.log(values);
                return {
                    results: $.map(response.content, function (item) {
                        if(!values.includes(item.id)) {
                            return {
                                text: item.name,
                                id: item.id
                            }
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
$("#tariff").on("change", function () {
    blockBy("#service-div");
    $.ajax({
        type: "GET",
        url: "get-tariff-items",
        data: {
            tariffId: $("#tariff").val()
        },
        success: function (response) {
            console.log(response)
            tariffServices = response;
            setPrices();
            calculateTotal();
            unblockBy("#service-div");
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
});
function setPrices() {
    $("#service-table").find("tr").each(function () {
        let select = $(this).find("select");
        setPricePerUnit(select);
    });
}

$("#set-for-tariffs").on("click", function () {
    blockBy("#service-div");
    drawServiceTable(tariffServices);
});
function drawServiceTable(response) {
    if (response === undefined || response.length === 0) {
        toastr.warning(chooseApartment);
        unblockBy("#service-div");
    } else {
        $("#service-table").children().remove();
        for (let service of response) {
            setRowInServiceTable();
            initializeServiceSelects(service);
            addListenerToSelects();
            let serviceOption = new Option(service.serviceName, service.serviceId, true, true);
            $("#service" + i).append(serviceOption).trigger('change');
            i++;
        }
        appendTotal();
        calculateTotal();
        unblockBy("#service-div");
    }
}
$("#set-amount").on("click", function () {
    if($("input[name=checks]:checked").length !== 0) {
        blockBy("#service-div");
        let serviceIds = [];
        $("input[name=checks]:checked").each(function () {
            let serviceId = $(this).parent().parent().find("select").val();
            serviceIds.push(serviceId);
        });
        getAmountOfConsumption(serviceIds);
    } else {
        toastr.warning(chooseService);
    }
});

function getAmountOfConsumption(serviceIds) {
    $.ajax({
        type: "GET",
        url: "get-amounts",
        data: {
            serviceIds: serviceIds,
            apartmentId: $("#apartmentId").val()
        },
        success: function (response) {
            console.log(response);
            setAmounts(response);
            unblockBy("#service-div");
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}

function setAmounts(response) {
    let j = 0;
    $("input[name=checks]:checked").each(function () {
        let quantityInput = $(this).parent().parent().find(".quantity");
        quantityInput.val(response[j]).trigger("input");
        j++;
    });
}

function appendTotal() {
    $("#service-table").append(
        `<tr>
            <td colspan="5"></td>
            <td colspan="2"><span id="total"></span><span id="totalPrice" onchange="changePaid()"></span></td>
        </tr>`
    );
}
function changePaid() {
    if($("#status").val() !== null && $("#status").val().localeCompare("PAID") === 0){
        $("#paid").val(Number($("#totalPrice").text()));
    }
}


function getReadings(currentPage) {
    blockBy("#dropdownParent1");
    $.ajax({
        type: "GET",
        url: "get-meter-readings",
        data: {
            page: currentPage,
            pageSize: tableLength,
            apartmentId: $("#apartmentId").val()
        },
        success: function (response) {
            console.log(response);
            meterReadings = response;
            $("#reading-table").children().remove();
            $(".card-footer").children().remove();
            drawReadingsTable(response);
            unblockBy("#dropdownParent1");
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}

function drawReadingsTable(response) {
    if (response.numberOfElements == 0) {
        $("#reading-table").append(`<tr class="tr"><td colspan="10" class="text-center">${dataNotFound}</td>></tr>`);
    } else {
        for (const reading of response.content) {
            var parts = reading.creationDate.split('.');
            let date = new Date(parts[2], parts[1] - 1, parts[0]).toLocaleString(dateLocale, {
                month: 'long',
                year: 'numeric'
            });
            $("#reading-table")
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
                    </tr>`);
        }
        if (response.totalPages > 0) {
            const page = response.pageable.pageNumber;
            drawPaginationElements(response, "getReadings");
            drawPagination(response.totalPages, page, 'getReadings');
        }
    }
}

function getStatusSpan(status) {
    switch (status) {
        case 'NEW':
            return '<span class="badge bg-label-info">' + newStatus + '</span>';
        case 'INCLUDED':
            return '<span class="badge bg-label-success">' + includedStatus + '</span>';
        case 'INCLUDED_AND_PAID':
            return '<span class="badge bg-label-primary">' + includedPaidStatus + '</span>';
        case 'ZERO':
            return '<span class="badge bg-label-warning">' + zeroStatus + '</span>';
    }
}

$("#status").on("change", function () {
    let payedInput = $("#paid");
    payedInput.prop('disabled', true);
    switch ($(this).val()) {
        case 'PAID':
            payedInput.val(Number($("#totalPrice").text()));
            break;
        case 'UNPAID':
            payedInput.val(0);
            break;
        case 'PARTLY_PAID':
            payedInput.val("");
            payedInput.prop('disabled', false);
            break;
    }
});

$("#save-button").on("click", function () {
    let formData = collectItemsData();
    appendInvoiceFields(formData);
    for (var pair of formData.entries()) {
        console.log(pair[0] + ': ' + pair[1]);
    }
    sendData(formData);
});

function collectItemsData() {
    let formData = new FormData();
    let ind = 0;
    if($("#service-table").find("tr").children("td").length !== 1) {
        $("#service-table").find("tr").not(':last').each(function () {
            let service = $(this).find(".select2");
            let serviceId = service.val() == null? '': service.val();
            formData.append("itemRequests[" + ind + "].serviceId", serviceId)
            service.attr("name", "itemRequests[" + ind + "].serviceId");
            let quantity = $(this).find(".quantity");
            formData.append("itemRequests[" + ind + "].amount", quantity.val())
            quantity.attr("name", "itemRequests[" + ind + "].amount");
            let pricePerUnit = $(this).find(".per-unit");
            formData.append("itemRequests[" + ind + "].pricePerUnit", pricePerUnit.val())
            pricePerUnit.attr("name", "itemRequests[" + ind + "].pricePerUnit");
            let cost = $(this).find(".cost");
            formData.append("itemRequests[" + ind + "].cost", cost.val())
            cost.attr("name", "itemRequests[" + ind + "].cost");
            ind++;
        });
    }
    return formData;
}

function appendInvoiceFields(formData) {
    let apartmentId = $("#apartmentId").val() == null? '': $("#apartmentId").val();
    formData.append("apartmentId",apartmentId);
    formData.append("creationDate",$("#creationDate").val());
    let status = $("#status").val() == null? '': $("#status").val();
    formData.append("status",status);
    formData.append("paid",$("#paid").val());
    let house = $("#house").val() == null? '': $("#house").val();
    formData.append("house",house);
    formData.append("isProcessed",$("#processed").is(':checked'));
    formData.append("totalPrice",$("#totalPrice").text());
}
function sendData(formData) {
    blockCardDody();
    clearAllErrorMessage();
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
    blockBy(".card-body");
    clearFields();
    $('#apartmentId').prop('disabled', true);
    $('#section').prop('disabled', true);
    unblockBy(".card-body");
});

function clearFields() {
    $("#personalAccount, #paid").val('');
    $("#owner, #phone-number").text('');
    $("#form").find('select').val(null).trigger('change');
    $("#service-table").children().remove();
    $("#service-table").append(
        `<tr>
            <td colSpan="7" class="text-center">${noData}</td>
        </tr>`
    );
}