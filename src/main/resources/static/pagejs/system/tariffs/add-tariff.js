let lastItemIndex = 0;

$(document).ready(drawIncludesServices);

function drawIncludesServices() {
    $('.tariff-items-list').empty();
    if (tariffCopy && tariffCopy.name) {
        tariffCopy.name += ' - 2'
        fillInputs(tariffCopy);
    } else {
        addTariffItem(lastItemIndex);
    }
}

$('input[name="tariffRequest.name"]').on('input', function () {
    $('#tariff-title').html(tariffTitle + ': ' + this.value);
})

$('.button-cancel').on('click', () => window.history.back())

function addTariffItem(index) {
    let $service = $(`<div class="row g-4 mb-3 tariff-item" id="item-${index}">
                        <input type="text" class="visually-hidden tariff-item-id" name="tariffRequest.tariffItems[${index}].id" 
                                id="tariffRequest.tariffItems[${index}].id">
                        <div class="mb-3 col-md-4">
                            <label class="form-label" for="tariffItems[${index}].serviceId">${serviceLabel}</label>
                            <select class="form-select" type="text" name="tariffRequest.tariffItems[${index}].serviceId"
                                    id="tariffItems[${index}].serviceId" placeholder="${serviceLabel}"></select>
                        </div>
                        <div class="mb-3 col-md-2">
                            <label class="form-label" for="tariffItems[${index}].servicePrice">${servicePriceLabel}</label>
                            <input class="form-control" type="text" name="tariffRequest.tariffItems[${index}].servicePrice"
                                    id="tariffItems[${index}].servicePrice" placeholder="${servicePriceLabel}">
                        </div>
                        <div class="mb-3 col-md-2">
                            <label class="form-label" for="tariffItems[${index}].currency">${serviceCurrency}</label>
                            <input class="form-control" type="text" name="tariffRequest.tariffItems[${index}].currency"
                                   id="tariffItems[${index}].currency" placeholder="${serviceCurrency}" disabled>
                        </div>
                        <div class="mb-3 col-md-2">
                            <label class="form-label" for="tariffItems[${index}].service.unitOfMeasurement.name">${unitLabel}</label>
                            <input class="form-control" type="text" name="tariffRequest.tariffItems[${index}].service.unitOfMeasurement.name"
                                   id="tariffItems[${index}].service.unitOfMeasurement.name" placeholder="${unitLabel}" disabled>
                        </div>
                    </div>`);
    if (index > 0) {
        $service.append(`<div class="mb-3 col-md-1 d-flex align-items-end">
                            <button type="button" class="btn btn-outline-danger delete-item">
                                <i class="ti ti-trash ti-xs me-1"></i>
                            </button>
                        </div>`);
    }
    $service.hide();
    $service.appendTo('.tariff-items-list');
    initButtonAndInputs($service, index);
    $service.show('');
}

function initButtonAndInputs(item, index) {
    $(item).find('.delete-item').on('click', deleteTariffItem);
    autosize(document.querySelector('textarea'));

    let serviceSelect = item.find('select');
    serviceSelect.select2({
        placeholder: serviceLabel,
        minimumResultsForSearch: -1,
        dropdownParent: $(item),
        ajax: {
            type: "GET",
            url: '../services/get-services',
            processResults: function (response) {
                return {
                    results: $.map(response, function (unit) {
                        return {
                            id: unit.id,
                            text: unit.name
                        }
                    })
                };
            }
        }
    });

    $(serviceSelect).on('change', function () {
        $.ajax({
            url: '../services/get-service-by-id/' + this.value,
            type: 'get',
            dataType: 'json',
            success: function (response) {
                $(item).find('[name="tariffRequest.tariffItems[' + index + '].service.unitOfMeasurement.name"]')
                    .val(response.unitOfMeasurement.name);
                $(item).find('[name="tariffRequest.tariffItems[' + index + '].currency"]').val('грн')
            },
            error: function (error) {
                toastr.error(errorMessage);
            }
        })
    })
}

$('#add-service').on('click', function () {
    lastItemIndex++;
    addTariffItem(lastItemIndex)
});


let tariffItemToDelete = [];

function deleteTariffItem() {
    const serviceItemBlock = $(this).closest('.tariff-item');
    serviceItemBlock.hide('');
    let serviceIdToDelete = serviceItemBlock.find('.tariff-item-id').attr('value');
    if (serviceIdToDelete) {
        tariffItemToDelete.push(Number(serviceIdToDelete));
    }

    setTimeout(function () {
        serviceItemBlock.remove();
        reorderServiceIndexes();
    }, 1000)
}

function reorderServiceIndexes() {
    let tariffItems = $('#tariffForm').find('.tariff-item');
    let index = 0;
    for (const tariffItem of tariffItems) {
        $(tariffItem).find('input, select').each(function (i, input) {
            $(input).attr('id', $(input).attr('id').replace(/(\d{1,3})/g, index));
            $(input).attr('name', $(input).attr('name').replace(/(\d{1,3})/g, index));
        })
        index++;
    }
    lastItemIndex = index;
}

$('#save-tariff').on('click', function () {
    blockCardDody();
    clearAllErrorMessage();

    let formData = new FormData($('#tariffForm')[0]);

    for (const tariffItemToDeleteElement of tariffItemToDelete) {
        formData.append('tariffItemToDelete[]', tariffItemToDeleteElement);
    }
    for (const formDatum of formData.entries()) {
        console.log(formDatum)
    }

    $.ajax({
        type: 'post',
        url: 'add-tariff',
        processData: false,
        contentType: false,
        data: formData,
        success: function (response) {
            toastr.success(successSaveMessage);
            window.history.back();
        },
        error: function (error) {
            console.log(error)
            printErrorMessageToField(error);
            toastr.error(errorSaveMessage)
        }
    });
});

function fillInputs(tariff) {
    $('input[name="tariffRequest.id"]').val(tariff.id);
    $('input[name="tariffRequest.name"]').val(tariff.name);
    $('textarea[name="tariffRequest.description"]').val(tariff.description);
    let index = 0;
    for (let tariffItem of tariff.tariffItems) {
        fillTariffItem(index, tariffItem)
        index++;
    }
    lastItemIndex = index;
}

function fillTariffItem(index, item) {
    let $service = $(`<div class="row g-4 mb-1 tariff-item" id="item-${index}">
                        <input type="text" class="visually-hidden tariff-item-id" name="tariffRequest.tariffItems[${index}].id" 
                                id="tariffRequest.tariffItems[${index}].id" value="${item.id}">
                        <div class="mb-3 col-md-4">
                            <label class="form-label" for="tariffItems[${index}].serviceId">${serviceLabel}</label>
                            <select class="form-select" type="text" name="tariffRequest.tariffItems[${index}].serviceId"
                                    id="tariffItems[${index}].serviceId" placeholder="${serviceLabel}"></select>
                        </div>
                        <div class="mb-3 col-md-2">
                            <label class="form-label" for="tariffItems[${index}].servicePrice">${servicePriceLabel}</label>
                            <input class="form-control" type="number" name="tariffRequest.tariffItems[${index}].servicePrice"
                                    id="tariffItems[${index}].servicePrice" placeholder="${servicePriceLabel}" 
                                    value="${item.servicePrice}">
                        </div>
                        <div class="mb-3 col-md-2">
                            <label class="form-label" for="tariffItems[${index}].currency">${serviceCurrency}</label>
                            <input class="form-control" type="text" name="tariffRequest.tariffItems[${index}].currency"
                                   id="tariffItems[${index}].currency" placeholder="${serviceCurrency}" 
                                   value="${item.currency}" disabled>
                        </div>
                        <div class="mb-3 col-md-2">
                            <label class="form-label" for="tariffItems[${index}].service.unitOfMeasurement.name">${unitLabel}</label>
                            <input class="form-control" type="text" name="tariffRequest.tariffItems[${index}].service.unitOfMeasurement.name"
                                   id="tariffItems[${index}].service.unitOfMeasurement.name" placeholder="${unitLabel}" 
                                   value="${item.service.unitOfMeasurement.name}" disabled>
                        </div>
                    </div>`);
    if (index > 0) {
        $service.append(`<div class="mb-3 col-md-1 d-flex align-items-end">
                            <button type="button" class="btn btn-outline-danger delete-item">
                                <i class="ti ti-trash ti-xs me-1"></i>
                            </button>
                        </div>`);
    }
    $service.hide();
    $service.appendTo('.tariff-items-list');
    fillButtonAndInputs($service, index, item);
    $service.show('fade');
}

function fillButtonAndInputs(itemBlock, index, tariffItem) {
    $(itemBlock).find('.delete-item').on('click', deleteTariffItem);
    autosize(document.querySelector('textarea'));

    let serviceSelect = itemBlock.find('select');
    const itemService = tariffItem.service;
    serviceSelect.select2({
        placeholder: serviceLabel,
        minimumResultsForSearch: -1,
        dropdownParent: $(itemBlock),
        data: [{
            id: itemService.id,
            text: itemService.name
        }],
        ajax: {
            type: "GET",
            url: '../../services/get-services',
            processResults: function (response) {
                return {
                    results: $.map(response, function (unit) {
                        return {
                            id: unit.id,
                            text: unit.name
                        }
                    })
                };
            }
        }
    });

    $(serviceSelect).on('change', function () {
        $.ajax({
            url: '../../services/get-service-by-id/' + this.value,
            type: 'get',
            dataType: 'json',
            success: function (response) {
                $(itemBlock).find('[name="tariffRequest.tariffItems[' + index + '].service.unitOfMeasurement.name"]')
                    .val(response.unitOfMeasurement.name);
                $(itemBlock).find('[name="tariffRequest.tariffItems[' + index + '].currency"]').val('грн')
            },
            error: function (error) {
                toastr.error(errorMessage);
            }
        })
    })
}
