let lastServiceIndex = 0;
let serviceListToRestore;

$(document).ready(getServices);
$('[aria-controls="navs-services"]').on('click', getServices);

function getServices() {
    blockBy('#servicesForm')
    $.ajax({
        type: 'get',
        url: 'services/get-services',
        success: function (response) {
            serviceListToRestore = response;
            drawFormServices(response);
            unblockBy('#servicesForm');
        },
        error: function (err) {
        }
    });
}

function drawFormServices(listService) {
    $('.service-list').empty();
    const serviceArray = Array.from(listService);
    if (listService.length > 0) {
        lastServiceIndex = listService.length;
        let i = 0;
        for (const service of serviceArray) {
            let $service = $(`<div class="row g-4 service-item" id="service-${i}">
                                <div class="mb-3 col-md-6">
                                    <label class="form-label" for="services[${i}].name">${serviceLabel}</label>
                                    <input class="form-control" type="text" name="services[${i}].name"
                                        id="services[${i}].name" value="${service.name}" placeholder="${serviceLabel}">
                                    <input type="number" class="visually-hidden" name="services[${i}].id"
                                        id="services[${i}].id" value="${service.id}">
                                </div>
                                <div class="mb-3 col-md-4">
                                    <label class="form-label" for="services[${i}].unitOfMeasurementId">${unitLabel}</label>
                                    <select class="form-select" type="text" name="services[${i}].unitOfMeasurementId" 
                                        id="services[${i}].unitOfMeasurementId" ></select>
                                </div>
                                <div class="mb-3 col-md-1 d-flex align-items-end">
                                    <button type="button" class="btn btn-outline-danger delete-service">
                                        <i class="ti ti-trash ti-xs me-1"></i>
                                    </button>
                                </div>
                                <div class="mb-3 col-md-6 mt-0">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" name="services[${i}].showInMeter"
                                            id="services[${i}].showInMeter"/>
                                        <label class="form-check-label" for="services[${i}].showInMeter">
                                            ${useOnServiceLabel}</label>
                                    </div>
                                </div>
                            </div>`);
            $service.hide();
            $service.appendTo('.service-list');
            $service.show('');
            $service.find('input.form-check-input').prop('checked', service.showInMeter);
            $service.find('select').select2({
                placeholder: unitLabel,
                minimumResultsForSearch: -1,
                dropdownParent: $service,
                data: [{
                    id: service.unitOfMeasurement.id,
                    text: service.unitOfMeasurement.name
                }],
                ajax: {
                    type: "GET",
                    url: 'services/get-measurement-units',
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
            i++;
        }
    } else {
        addNewService();
    }
    $('.delete-service').on('click', deleteService);
}

$('#add-service').on('click', addNewService);

function addNewService() {
    let $service = $(`<div class="row g-4 service-item" id="service-${lastServiceIndex}">
                                <div class="mb-3 col-md-6">
                                    <label class="form-label" for="services[${lastServiceIndex}].name">${serviceLabel}</label>
                                    <input class="form-control" type="text" name="services[${lastServiceIndex}].name"
                                        id="services[${lastServiceIndex}].name" placeholder="${serviceLabel}">
                                    <input type="number" class="visually-hidden service-id" name="services[${lastServiceIndex}].id"
                                        id="services[${lastServiceIndex}].id">
                                </div>
                                <div class="mb-3 col-md-4">
                                    <label class="form-label" for="services[${lastServiceIndex}].unitOfMeasurementId">${unitLabel}</label>
                                    <select class="form-select" type="text" name="services[${lastServiceIndex}].unitOfMeasurementId" 
                                        id="services[${lastServiceIndex}].unitOfMeasurementId" ></select>
                                </div>
                                <div class="mb-3 col-md-1 d-flex align-items-end">
                                    <button type="button" class="btn btn-outline-danger delete-service">
                                        <i class="ti ti-trash ti-xs me-1"></i>
                                    </button>
                                </div>
                                <div class="mb-3 col-md-6 mt-0">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" name="services[${lastServiceIndex}].showInMeter"
                                            id="services[${lastServiceIndex}].showInMeter"/>
                                        <label class="form-check-label" for="services[${lastServiceIndex}].showInMeter">
                                            ${useOnServiceLabel}</label>
                                    </div>
                                </div>
                            </div>`);
    $service.hide();
    $service.appendTo('.service-list');
    $service.show('');

    $service.find('.delete-service').on('click', deleteService);
    $service.find('select').select2({
        placeholder: unitLabel,
        minimumResultsForSearch: -1,
        dropdownParent: $service,
        ajax: {
            type: "GET",
            url: 'services/get-measurement-units',
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

    lastServiceIndex++;
}

let serviceToDelete = [];

function deleteService() {
    const serviceItemBlock = $(this).closest('.service-item');
    let serviceIdToDelete = serviceItemBlock.find('.service-id').attr('value');
    if (serviceIdToDelete) {
        serviceToDelete.push(Number(serviceIdToDelete));
    }
    serviceItemBlock.hide('');
    setTimeout(function () {
        serviceItemBlock.remove();
        reorderServiceIndexes();
    }, 500)
}

function reorderServiceIndexes() {
    let unitItems = $('#servicesForm').find('.service-item');
    let index = 0;
    for (const unitItem of unitItems) {
        $(unitItem).find('input').each(function (i, input) {
            $(input).attr('id', $(input).attr('id').replace(/(\d{1,3})/g, index));
            $(input).attr('name', $(input).attr('name').replace(/(\d{1,3})/g, index));
        })
        index++;
    }
    lastUnitIndex = index;
}

$('#save-services').on('click', function () {
    blockBy('#servicesForm');
    clearAllErrorMessage();

    let formData = new FormData($('#servicesForm')[0]);
    $('input.form-check-input').each(function (i, input) {
        formData.set($(input).attr('name'), $(input).prop('checked'));
    })
    for (const servicesToDeleteElement of serviceToDelete) {
        formData.append('serviceToDelete[]', servicesToDeleteElement);
    }
    for (const formDatum of formData.entries()) {
        console.log(formDatum)
    }

    $.ajax({
        type: 'post',
        url: 'services/update-services',
        processData: false,
        contentType: false,
        data: formData,
        success: function (response) {
            toastr.success(successSaveMessage);
            unblockBy('#servicesForm');
        },
        error: function (error) {
            console.log(error)
            printErrorMessageToField(error);
            unblockBy('#servicesForm');
            toastr.error(errorSaveMessage)
        }
    });
});

$('#servicesForm .button-cancel').on('click', function () {
    drawFormServices(serviceListToRestore);
})

// ### Units of measurement
let lastUnitIndex = 0;
let listUnitsToRestore;

$('[aria-controls="navs-units"]').on('click', function () {
    blockBy('#measurementUnist');
    $.ajax({
        type: 'get',
        url: 'services/get-measurement-units',
        success: function (response) {
            listUnitsToRestore = response;
            drawFormUnits(response);
            unblockBy('#measurementUnist');
        },
        error: function (err) {
            unblockBy('#measurementUnist');
        }
    });
});

function drawFormUnits(listUnits) {
    $('.unit-item-list').empty();
    const unitsArray = Array.from(listUnits);
    if (listUnits.length > 0) {
        lastUnitIndex = listUnits.length;
        let i = 0;
        for (const unit of unitsArray) {
            $(`<div class="row g-4 unit-item" id="item-${i}">\n` +
                `   <div class="mb-3 col-md-6" >\n` +
                `        <label class="form-label">${unitLabel}</label>\n` +
                `        <div class="input-group">\n` +
                `             <input class="form-control" type="text" name="unitOfMeasurements[${i}].name" \n` +
                `                 id="unitOfMeasurements[${i}].name" value="${unit.name}" \n` +
                `                    placeholder="${unitLabel}">\n` +
                `             <button type="button" class="btn btn-outline-danger input-group-text delete-unit">\n` +
                `                     <i class="ti ti-trash ti-xs me-1"></i>\n` +
                `                     <span class="align-middle"></span>\n` +
                `             </button>\n` +
                `         </div>\n` +
                `         <input type="number" class="visually-hidden unit-id" id="unitOfMeasurements[${i}].id"` +
                `                 name="unitOfMeasurements[${i}].id" value="${unit.id}">\n` +
                `   </div>\n` +
                `</div>`).appendTo('.unit-item-list');
            i++;
        }
    } else {
        addNewUnit();
    }
    $('.delete-unit').on('click', deleteUnits);
}

$('#add-unit').on('click', addNewUnit);

function addNewUnit() {
    let $item = $(`<div class="row g-4 unit-item" id="item-${lastUnitIndex}">\n` +
        `   <div class="mb-3 col-md-6" >\n` +
        `        <label class="form-label">${unitLabel}</label>\n` +
        `        <div class="input-group">\n` +
        `             <input class="form-control" type="text" name="unitOfMeasurements[${lastUnitIndex}].name" \n` +
        `                 id="unitOfMeasurements[${lastUnitIndex}].name"\n` +
        `                    placeholder="${unitLabel}">\n` +
        `             <button type="button" class="btn btn-outline-danger input-group-text delete-unit"` +
        `                   >\n` +
        `                     <i class="ti ti-trash ti-xs me-1"></i>\n` +
        `                     <span class="align-middle"></span>\n` +
        `             </button>\n` +
        `         </div>\n` +
        `         <input type="number" class="visually-hidden unit-id" id="unitOfMeasurements[${lastUnitIndex}].id"` +
        `                 name="unitOfMeasurements[${lastUnitIndex}].id">\n` +
        `   </div>\n` +
        `</div>`);
    $item.hide('');
    $item.appendTo('.unit-item-list');
    $item.show('');
    $item.find('.delete-unit').on('click', deleteUnits);

    lastUnitIndex++;
}

let unitsToDelete = [];

function deleteUnits() {
    const unitItemBlock = $(this).closest('.unit-item');
    let unitIdToDelete = unitItemBlock.find('.unit-id').attr('value');
    if (unitIdToDelete) {
        unitsToDelete.push(Number(unitIdToDelete));
    }

    unitItemBlock.hide('');
    setTimeout(function () {
        unitItemBlock.remove();
        reorderIndexes();
    }, 500)
}

function reorderIndexes() {
    let unitItems = $('#measurementUnist').find('.unit-item');
    let index = 0;
    for (const unitItem of unitItems) {
        $(unitItem).find('input').each(function (i, input) {
            $(input).attr('id', $(input).attr('id').replace(/(\d{1,3})/g, index));
            $(input).attr('name', $(input).attr('name').replace(/(\d{1,3})/g, index));
        })
        index++;
    }
    lastUnitIndex = index;
}

$('#save-units').on('click', function () {
    clearAllErrorMessage();
    blockBy('#measurementUnist');

    let formData = new FormData($('#measurementUnist')[0]);
    for (const unitsToDeleteElement of unitsToDelete) {
        formData.append('unitsToDelete[]', unitsToDeleteElement);
    }

    for (const formDatum of formData.entries()) {
        console.log(formDatum)
    }

    $.ajax({
        type: 'post',
        url: 'services/update-measurement-unist',
        processData: false,
        contentType: false,
        data: formData,
        success: function (response) {
            toastr.success(successSaveMessage);
            unblockBy('#measurementUnist');
        },
        error: function (error) {
            console.log(error)
            printErrorMessageToField(error);
            unblockBy('#measurementUnist');
            toastr.error(errorSaveMessage)
        }
    });
});

$('#measurementUnist .button-cancel').on('click', function () {
    drawFormUnits(listUnitsToRestore);
})
