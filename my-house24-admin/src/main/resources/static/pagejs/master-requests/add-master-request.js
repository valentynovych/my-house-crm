$(document).ready(function () {
    initInputAndSelect();
})
const $inputVisitDate = $('[name="visitDate"]');
const $inputVisitTime = $('[name="visitTime"]');
const $selectApartmentOwner = $('[name="apartmentOwnerId"]');
const $inputDescription = $('[name="description"]');
const $selectApartment = $('[name="apartmentId"]');
const $selectMasterType = $('[name="masterType"]');
const $selectStatus = $('[name="status"]');
const $selectStaff = $('[name="masterId"]');
const $inputComment = $('[name="comment"]');
Date.prototype.addMinutes = function (minutes) {
    this.setMinutes((Math.round(this.getMinutes() / 10) * 10) + minutes)
}

const date = new Date();
date.addMinutes(40);

const $flatpickrDate = flatpickr($inputVisitDate, {
    dateFormat: "d.m.Y",
    minDate: 'today',
    defaultDate: date
});

const $flatpickrTime = flatpickr($inputVisitTime, {
    enableTime: true,
    noCalendar: true,
    minuteIncrement: 10,
    time_24hr: true,
    minTime: '9:30',
    maxTime: '21:30',
    // defaultDate: date,
});
$flatpickrTime.setDate(date);

function initInputAndSelect() {

    $selectApartmentOwner.select2({
        dropdownParent: $('#apartmentOwnerId-wrap'),
        maximumInputLength: 50,
        placeholder: chooseApartmentOwner,
        ajax: {
            type: "GET",
            url: '../owners/get-owners',
            data: function (params) {
                return {
                    fullName: params.term,
                    page: (params.page - 1) || 0,
                    pageSize: 10
                };
            },
            processResults: function (response) {
                return {
                    results: $.map(response.content, function (owner) {
                        return {
                            id: owner.id,
                            text: owner.fullName,
                            phoneNumber: owner.phoneNumber
                        }
                    }),
                    pagination: {
                        more: !response.last
                    }
                };
            }
        }
    });

    $selectApartmentOwner.on('change', function () {
        let ownerData = $selectApartmentOwner.select2('data')[0];
        $('#apartment-owner-phone').html(ownerData.phoneNumber);

        resetSelectsAndFields();
        initSelectApartment(ownerData.id, false);
    })

    function resetSelectsAndFields() {
        $selectApartment.val(null).trigger('change');
        $('#house').html('');
        $('#section').html('');
        $('#floor').html('');
    }

    function initSelectApartment(ownerId, isDisabled) {
        $selectApartment.select2({
            dropdownParent: $('#apartmentId-wrap'),
            maximumInputLength: 50,
            placeholder: chooseApartment,
            disabled: isDisabled,
            ajax: {
                type: "GET",
                url: `../apartments/get-apartments`,
                data: function (params) {
                    return {
                        apartmentNumber: params.term,
                        page: (params.page - 1) || 0,
                        pageSize: 10,
                        owner: ownerId
                    };
                },
                processResults: function (response) {
                    console.log(response)
                    return {
                        results: $.map(response.content, function (apartment) {
                            return {
                                id: apartment.id,
                                text: (apartment.apartmentNumber).padStart(5, '00000'),
                                house: apartment.house,
                                section: apartment.section,
                                floor: apartment.floor
                            }
                        }),
                        pagination: {
                            more: !response.last
                        }
                    };
                }
            }
        });
    }

    $selectApartment.on('select2:select', function () {
        const apartmentData = $selectApartment.select2('data')[0];

        const house = apartmentData.house;
        const houseLink = `<a href="../houses/view-house/${house.id}">${house.name}</a>`
        $('#house').html(houseLink);

        $('#section').html(apartmentData.section.name);
        $('#floor').html(apartmentData.floor.name);
    })


    $selectMasterType.select2({
        dropdownParent: $('#masterType-wrap'),
        minimumResultsForSearch: -1,
        maximumInputLength: 50,
        placeholder: chooseMasterType,
    });
    const electricianOption = new Option(getRoleLabel('ELECTRICIAN'), 'ELECTRICIAN', false, false);
    const plumberOption = new Option(getRoleLabel('PLUMBER'), 'PLUMBER', false, false);
    const emptyOption = new Option('', '', false, false);

    $selectMasterType.append(emptyOption);
    $selectMasterType.append(electricianOption);
    $selectMasterType.append(plumberOption);

    $selectStatus.select2({
        dropdownParent: $('#status-wrap'),
        minimumResultsForSearch: -1,
        maximumInputLength: 50,
        placeholder: chooseMasterType,
    });

    const newOption = new Option(getStatusLabel('NEW'), 'NEW', false, false);
    const inProgressOption = new Option(getStatusLabel('IN_PROGRESS'), 'IN_PROGRESS', false, false);
    const doneOption = new Option(getStatusLabel('DONE'), 'DONE', false, false);
    const canceledOption = new Option(getStatusLabel('CANCELED'), 'CANCELED', false, false);

    $selectStatus.append(newOption);
    $selectStatus.append(inProgressOption);
    $selectStatus.append(doneOption);
    $selectStatus.append(canceledOption);

    $selectMasterType.on('select2:select', function () {
        const status = $selectMasterType.select2('data')[0];
        initSelectStaff(status, false);
    })

    function initSelectStaff(status, isDisabled) {

        $selectStaff.select2({
            dropdownParent: $('#masterId-wrap'),
            maximumInputLength: 50,
            placeholder: chooseMaster,
            disabled: isDisabled,
            ajax: {
                type: "GET",
                url: '../system-settings/staff/get-staff',
                data: function (params) {
                    return {
                        name: params.term,
                        page: (params.page - 1) || 0,
                        pageSize: 10,
                        roleName: status.id
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
    }

    autosize($inputComment);
    autosize($inputDescription);
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

function getStatusLabel(role) {
    switch (role) {
        case 'NEW':
            return labelStatusNew;
        case 'DONE':
            return labelStatusDone;
        case 'IN_PROGRESS':
            return labelStatusInProgress;
        case 'CANCELED':
            return labelStatusCanceled;
    }
}

$('.button-save').on('click', function () {
    clearAllErrorMessage();
    blockCardDody();
    const dateString = `${$inputVisitDate.val()}T${$inputVisitTime.val()}:00`;
    console.log($flatpickrDate.selectedDates)
    console.log(dateString)
    const val = $inputVisitTime.val().split(':');
    const visitDate = new Date(Date.parse($flatpickrDate.selectedDates));
    visitDate.setHours(val[0]);
    visitDate.setMinutes(val[1]);

    let formData = new FormData($('#master-request-form')[0]);
    formData.set('apartmentOwnerPhone', $('#apartment-owner-phone').html());
    formData.set('visitDate', visitDate.getTime())
    //
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
            window.history.back();
        },
        error: function (error) {
            printErrorMessageToField(error);
            toastr.error(errorMessage)
        }
    });
});

$('.button-cancel').on('click', () => window.history.back())