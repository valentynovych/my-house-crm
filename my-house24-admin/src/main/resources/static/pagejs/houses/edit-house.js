let lastSectionIndex = 0;
let lastFloorIndex = 0;
let lastStaffIndex = 0;
const houseId = window.location.pathname.match('\\d+');
let houseToRestore;

$('#add-section').on('click', () => addNewSection({name: '', rangeApartmentNumbers: '', id: ''}));
$('#add-floor').on('click', () => addNewFloor({name: '', id: ''}));
$('#add-staff').on('click', () => addNewStaff(
    {id: '', firstName: '', lastName: '', role: {name: ''}}));

$('.cancel-button').on('click', function () {
    if (houseToRestore) {
        fillInputs(houseToRestore)
    } else {
        toastr.error(errorMessage);
    }
});

$(window).on('load', function () {
    blockCardDody();
    $.ajax({
        url: '../../houses/get-house/' + houseId,
        method: 'get',
        success: function (houseResponse) {
            houseToRestore = houseResponse;
            fillInputs(houseResponse);
        },
        error: function (error) {
            console.log(error);
            toastr.error(errorMessage);
        }
    })

})

function fillInputs(house) {
    $('.section-list, .floor-list, .staff-list').children().remove();
    $('#view-house-link').attr('href', `../view-house/${houseId}`);
    $('title, #house-title, #view-house-link').html(house.name);
    $('#id').val(house.id);
    $('#name').val(house.name);
    $('#address').val(house.address);

    $('#image1').attr('src', house.image1 ? uploadsPath + house.image1 : placeholderImage);
    $('#image2').attr('src', house.image2 ? uploadsPath + house.image2 : placeholderImage);
    $('#image3').attr('src', house.image3 ? uploadsPath + house.image3 : placeholderImage);
    $('#image4').attr('src', house.image4 ? uploadsPath + house.image4 : placeholderImage);
    $('#image5').attr('src', house.image5 ? uploadsPath + house.image5 : placeholderImage);

    for (const section of house.sections) {
        addNewSection(section);
    }
    for (const floor of house.floors) {
        addNewFloor(floor);
    }
    for (const staff of house.staff) {
        addNewStaff(staff);
    }

}

function addNewSection(section) {
    let $section = $(`<div class="row g-4 section-item" id="section-${lastSectionIndex}">
                        <div class="mb-3 col-md-8">
                            <label class="form-label" for="sections[${lastSectionIndex}].name">${houseSectionLabel}</label>
                            <input type="text" class="visually-hidden id" name="sections[${lastSectionIndex}].id" 
                                value="${section.id}">
                                <input type="checkbox" class="visually-hidden" name="sections[${lastSectionIndex}].deleted">
                            <div class="input-group">
                                <input class="form-control" type="text"
                                       name="sections[${lastSectionIndex}].name" id="sections[${lastSectionIndex}].name"
                                       placeholder="${houseSectionLabel}" value="${section.name}">
                            </div>
                        </div>
                        <div class="mb-3 col-md-4">
                            <label class="form-label" for="sections[${lastSectionIndex}].rangeApartmentNumbers">
                                ${houseSectionHouseRangeLabel}</label>
                            <div class="input-group">
                                <input class="form-control range-number" type="text"
                                       name="sections[${lastSectionIndex}].rangeApartmentNumbers"
                                       id="sections[${lastSectionIndex}].rangeApartmentNumbers"
                                       placeholder="001-101" value="${section.rangeApartmentNumbers}">
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

function addNewFloor(floor) {
    let $floor = $(`<div class="row g-4 floor-item" id="floor-${lastFloorIndex}">
                        <div class="mb-3">
                            <label class="form-label" for="floors[${lastFloorIndex}].name">
                                ${houseFloorLabel}</label>
                                <input type="text" class="visually-hidden id" name="floors[${lastFloorIndex}].id" 
                                value="${floor.id}">
                                <input type="checkbox" class="visually-hidden" name="floors[${lastFloorIndex}].deleted">
                            <div class="input-group">
                                <input class="form-control" type="text"
                                       name="floors[${lastFloorIndex}].name" id="floors[${lastFloorIndex}].name"
                                       placeholder="${houseFloorLabel}" value="${floor.name}">
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

function addNewStaff(staff) {
    let $staff = $(`<div class="row g-4 staff-item" id="staff-${lastStaffIndex}">
                        <div class="mb-3 d-flex gap-3">
                            <div class="col-7">
                                <label class="form-label" for="staff[${lastStaffIndex}].id">
                                ${houseStaffNameLabel}</label>
                                <select class="form-control " name="staff[${lastStaffIndex}].id" 
                                id="staff[${lastStaffIndex}].id">
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
    initStaffSelect2(lastStaffIndex, staff);
    lastStaffIndex++;
}

function initStaffSelect2(index, staff) {
    let $staffSelect = $(`[name="staff[${index}].id"]`);
    $staffSelect.select2({
        placeholder: houseStaffNameLabel,
        maximumInputLength: 50,
        data: [{
            id: staff.id,
            text: (staff.firstName + ' ' + staff.lastName),
            selected: true
        }],
        ajax: {
            type: "GET",
            url: '../../system-settings/staff/get-staff',
            data: function (params) {
                return {
                    name: params.term || '',
                    page: (params.page - 1) || 0,
                    pageSize: 10
                };
            },
            processResults: function (response) {
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

    let roleLabel = getRoleLabel(staff.role.name);
    $(`[name="staffs[${index}].role"]`).val(roleLabel);

    $staffSelect.on('change', function () {
        $.ajax({
            type: "GET",
            url: '../../system-settings/staff/get-staff/' + $(this).val(),
            success: function (response) {
                let roleLabel = getRoleLabel(response.role.name);
                $(`[name="staffs[${index}].role"]`).val(roleLabel);
            },
            error: function () {
                toastr.error(errorMessage);
            }
        })
    });
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
    sectionItem.find('input[type="checkbox"]').prop('checked', true);
    const sectionId = sectionItem.find('.id').attr('value');

    if (!sectionId) {
        setTimeout(function () {
            sectionItem.remove();
            reorderIndexes('.section-item');
        }, 1000);
    }
}

function deleteFloor() {
    const floorItem = $(this).closest('.floor-item');
    floorItem.hide('');
    floorItem.find('input[type="checkbox"]').prop('checked', true);
    const sectionId = floorItem.find('.id').attr('value');

    if (!sectionId) {
        setTimeout(function () {
            floorItem.remove();
            reorderIndexes('.floor-item');
        }, 1000);
    }

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
            setTimeout(function () {
                window.history.back()
            }, 400);
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
