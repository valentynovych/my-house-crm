let lastSectionIndex = 1;
let lastFloorIndex = 1;
let lastStaffIndex = 1;

$('#add-section').on('click', () => addNewSection());
$('#add-floor').on('click', () => addNewFloor());
$('#add-staff').on('click', () => addNewStaff());

$(window).on('load', function () {
    new Cleave(".range-number", {
        numericOnly: true,
        delimiter: '-',
        blocks: [3, 3]
    });
    initStaffSelect2(`0`);
})

function addNewSection() {
    let $section = $(`<div class="row g-4 section-item" id="section-${lastSectionIndex}">
                        <div class="mb-3 col-md-8">
                            <label class="form-label" for="sections[${lastSectionIndex}].name">${houseSectionLabel}</label>
                            <div class="input-group">
                                <input class="form-control" type="text"
                                       name="sections[${lastSectionIndex}].name" id="sections[${lastSectionIndex}].name"
                                       placeholder="${houseSectionLabel}">
                            </div>
                        </div>
                        <div class="mb-3 col-md-4">
                            <label class="form-label" for="sections[${lastSectionIndex}].rangeApartmentNumbers">
                                ${houseSectionHouseRangeLabel}</label>
                            <div class="input-group">
                                <input class="form-control range-number" type="text"
                                       name="sections[${lastSectionIndex}].rangeApartmentNumbers"
                                       id="sections[${lastSectionIndex}].rangeApartmentNumbers"
                                       placeholder="001-101">
                                <button type="button"
                                        class="btn btn-outline-danger input-group-text delete-section">
                                    <i class="ti ti-trash ti-xs me-1"></i>
                                    <span class="align-middle"></span>
                                </button>
                            </div>
                        </div>
                    </div>`);
    $section.hide();
    $section.appendTo('.section-list');
    $section.find('.delete-section').on('click', deleteSection);
    $section.show('');

    if (lastSectionIndex === 0) {
        $section.find('.delete-section').remove();
    }

    new Cleave(`[name="sections[${lastSectionIndex}].rangeApartmentNumbers"]`, {
        numericOnly: true,
        delimiter: '-',
        blocks: [3, 3]
    });

    lastSectionIndex++;
}

function addNewFloor() {
    let $floor = $(`<div class="row g-4 floor-item" id="floor-${lastFloorIndex}">
                        <div class="mb-3">
                            <label class="form-label" for="floors[${lastFloorIndex}].name">
                                ${houseFloorLabel}</label>
                            <div class="input-group">
                                <input class="form-control" type="text"
                                       name="floors[${lastFloorIndex}].name" id="floors[${lastFloorIndex}].name"
                                       placeholder="${houseFloorLabel}">
                                <button type="button"
                                        class="btn btn-outline-danger input-group-text delete-floor">
                                    <i class="ti ti-trash ti-xs me-1"></i>
                                    <span class="align-middle"></span>
                                </button>
                            </div>
                        </div>
                    </div>`);
    $floor.hide();
    $floor.appendTo('.floor-list');
    $floor.find('.delete-floor').on('click', deleteFloor);
    $floor.show('');

    if (lastFloorIndex === 0) {
        $floor.find('.delete-section').remove();
    }
    lastFloorIndex++;
}

function addNewStaff() {
    let $staff = $(`<div class="row g-4 staff-item" id="staff-${lastStaffIndex}">
                        <div class="mb-3 d-flex gap-3">
                            <div class="col-7">
                                <label class="form-label" for="staffIds[${lastStaffIndex}]">
                                ${houseStaffNameLabel}</label>
                                <select class="form-control " name="staffIds[${lastStaffIndex}]" 
                                id="staffIds[${lastStaffIndex}]">
                                    <option value="0">${houseStaffChooseStaff}</option>
                                </select>
                            </div>
                            <div class="col-5">
                                <label class="form-label" for="staffs[${lastStaffIndex}].role">
                                ${houseStaffRoleLabel}</label>
                                <div class="input-group">
                                    <input class="form-control" type="text"
                                           name="staffs[${lastStaffIndex}].role" id="staffs[${lastStaffIndex}].role" disabled>
                                    <button type="button"
                                            class="btn btn-outline-danger input-group-text delete-staff">
                                        <i class="ti ti-trash ti-xs me-1"></i>
                                        <span class="align-middle"></span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>`);
    $staff.hide();
    $staff.appendTo('.staff-list');
    $staff.find('.delete-staff').on('click', deleteStaff);
    $staff.show('');

    if (lastStaffIndex === 0) {
        $staff.find('.delete-staff').remove();
    }
    initStaffSelect2(lastStaffIndex);
    lastStaffIndex++;
}

function initStaffSelect2(index) {
    console.log('connect to: ' + index)
    let $staffSelect = $(`[name="staffIds[${index}]"]`);
    $staffSelect.select2({
        placeholder: houseStaffNameLabel,
        // minimumResultsForSearch: -1,
        maximumInputLength: 50,
        ajax: {
            type: "GET",
            url: '../system-settings/staff/get-staff',
            data: function (params) {
                return {
                    name: params.term || '',
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

    $staffSelect.on('change', function () {
        $.ajax({
            type: "GET",
            url: '../system-settings/staff/get-staff/' + $(this).val(),
            success: function (response) {
                let roleLabel = getRoleLabel(response.role.name);
                $(`[name="staffs[${index}].role"]`).val(roleLabel);
            },
            error: function () {
                toastr.error(errorMessage);
            }
        })
    });

    if (index === '0') {
        $.ajax({
            type: "GET",
            url: '../system-settings/staff/get-staff/1',
            success: function (response) {
                let option = new Option(response.firstName + ' ' + response.lastName, response.id, true, true);
                $staffSelect.append(option).trigger('change');

            },
            error: function () {
                toastr.error(errorMessage);
            }
        })
    }
}

function getRoleLabel(role) {
    switch (role) {
        case 'DIRECTOR':
            return roleDirector;
        case 'MANAGER':
            return roleManager;
        case 'ACCOUNTANT':
            return roleAccountant;
        case 'ELECTRICIAN':
            return roleElectrician;
        case 'PLUMBER':
            return rolePlumber;
    }
}

function deleteSection() {
    const sectionItem = $(this).closest('.section-item');
    sectionItem.hide('');
    setTimeout(function () {
        sectionItem.remove();
        reorderIndexes('.section-item');
    }, 1000);

}

function deleteFloor() {
    const floorItem = $(this).closest('.floor-item');
    floorItem.hide('');
    setTimeout(function () {
        floorItem.remove();
        reorderIndexes('.floor-item');
    }, 1000);

}

function deleteStaff() {
    const staffItem = $(this).closest('.staff-item');
    staffItem.hide('');
    setTimeout(function () {
        staffItem.remove();
        reorderIndexes('.staff-item');
    }, 1000);

}

function reorderIndexes(itemSelector) {
    let unitItems = $(`${itemSelector}`);
    let index = 0;
    for (const unitItem of unitItems) {
        $(unitItem).find('input, select').each(function (i, input) {
            $(input).attr('id', $(input).attr('id').replace(/(\d{1,3})/g, index));
            $(input).attr('name', $(input).attr('name').replace(/(\d{1,3})/g, index));
        })

        $(unitItem).find('.form-label').each(function (i, label) {
            $(label).attr('for', $(label).attr('for').replace(/(\d{1,3})/g, index));
        });
        index++;
    }

    switch (itemSelector) {
        case '.section-item':
            lastSectionIndex = index;
            break;
        case '.floor-item':
            lastFloorIndex = index;
            break
        case '.staff-item':
            lastStaffIndex = index;
            break;
    }
}

$('input[type="file"]').on('change', function () {
    const idToShow = $(this).attr('data-show');
    const $showPreview = $(idToShow);
    if (this.files && this.files[0]) {
        const type = this.files[0].type;
        const reader = new FileReader();
        reader.onload = function (e) {
            if (type === "image/jpeg" || type === "image/jpg" || type === "image/png") {
                $showPreview.attr("src", e.target.result);
                $showPreview.removeClass('is-invalid');
                $('.error-message').remove();
            } else {
                $showPreview.addClass("is-invalid").parent()
                    .after($('<p class="error-message text-center text-danger m-0">' +
                        'Вибраний файл не є зображенням у форматі jpeg, jpg або png.</p>'
                    ))
            }
        };
        reader.readAsDataURL(this.files[0]);
    } else {
        $showPreview.attr('src', placeholderImage);
        // window.location.origin + '/' + window.location.pathname.split('/')[1] + '/assets/img/backgrounds/2.jpg');
    }
})

$('.save-button').on('click', function () {
    clearAllErrorMessage();
    blockBy('#houseForm');
    $('button.bg-label-danger').removeClass('bg-label-danger');

    let formData = new FormData($('#houseForm')[0]);

    for (const formDatum of formData.entries()) {
        console.log(formDatum)
    }

    $.ajax({
        type: 'post',
        url: '',
        processData: false,
        contentType: false,
        data: formData,
        success: function (response) {
            toastr.success(successSaveMessage);
            unblockBy('#houseForm');
            setTimeout(() => {
                window.history.back();
            }, 500)
        },
        error: function (error) {
            console.log(error)
            printErrorMessageToField(error);
            unblockBy('#houseForm');
            additionalValidation(error);
            toastr.error(errorSaveMessage)
        }
    });
});

function additionalValidation(error) {
    if (error.status === 400) {
        const errorMap = new Map(Object.entries((error.responseJSON)));
        if (errorMap.has("images")) {
            const images = errorMap.get('images');
            $('[name="images[0]"]').addClass("is-invalid").parent().append($(
                '<p class="error-message invalid-feedback m-0">' + images + '</p>'
            ));
        }
        for (const key of errorMap.keys()) {
            if (key.includes('sections')) {
                $('button[aria-controls="navs-sections"]').addClass('bg-label-danger');
            }
            if (key.includes('floors')) {
                $('button[aria-controls="navs-floors"]').addClass('bg-label-danger');
            }
            if (key.includes('staffIds')) {
                $('button[aria-controls="navs-staff"]').addClass('bg-label-danger');
            }
        }
    }
}
