const masterRequestId = window.location.pathname.match(/\d+/);
let masterRequestToRestore;

const $inputVisitDate = $('[name="visitDate"]');
const $inputVisitTime = $('[name="visitTime"]');
const $selectApartmentOwner = $('[name="apartmentOwnerId"]');
const $inputDescription = $('[name="description"]');
const $selectApartment = $('[name="apartmentId"]');
const $selectMasterType = $('[name="masterType"]');
const $selectStatus = $('[name="status"]');
const $selectStaff = $('[name="masterId"]');
const $inputComment = $('[name="comment"]');

$(document).ready(function () {
    blockCardDody();
    $.ajax({
        url: '../get-request/' + masterRequestId,
        type: 'get',
        success: function (response) {
            console.log(response);
            masterRequestToRestore = response;
            fillInputs(response);
        },
        error: function (error) {
            console.log(error)
        }
    })
})

let $flatpickrDate;
let $flatpickrTime;

function fillInputs(request) {
    const date = new Date(request.visitDate * 1000);
    $flatpickrDate = flatpickr($inputVisitDate, {
        dateFormat: "d.m.Y",
    });
    $flatpickrTime = flatpickr($inputVisitTime, {
        enableTime: true,
        noCalendar: true,
        minuteIncrement: 10,
        time_24hr: true,
        minTime: '9:30',
        maxTime: '21:30',
    });
    const apartment = request.apartment;

    $flatpickrDate.setDate(date);
    $flatpickrTime.setDate(date);

    $selectApartmentOwner.select2({
        dropdownParent: $('#apartmentOwnerId-wrap'),
        maximumInputLength: 50,
        placeholder: chooseApartmentOwner,
        data: [{
            id: apartment.owner.id,
            text: apartment.owner.fullName,
            phoneNumber: apartment.owner.phoneNumber
        }],
        ajax: {
            type: "GET",
            url: '../../owners/get-owners',
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
        initSelectApartment(ownerData.id, false);
    })
    $selectApartmentOwner.trigger('change');

    function initSelectApartment(ownerId, isDisabled) {
        $selectApartment.select2({
            dropdownParent: $('#apartmentId-wrap'),
            maximumInputLength: 50,
            placeholder: chooseApartment,
            disabled: isDisabled,
            data: [{
                id: apartment.id,
                text: (apartment.apartmentNumber).padStart(5, '00000'),
                house: apartment.house,
                section: apartment.section,
                floor: apartment.floor
            }],
            ajax: {
                type: "GET",
                url: `../../apartments/get-apartments`,
                data: function (params) {
                    return {
                        apartmentNumber: params.term,
                        page: (params.page - 1) || 0,
                        pageSize: 10,
                        owner: ownerId
                    };
                },
                processResults: function (response) {
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

    $selectApartment.on('change', function () {
        const apartmentData = $selectApartment.select2('data')[0];

        const house = apartmentData.house;
        const houseLink = `<a href="../../houses/view-house/${house.id}">${house.name}</a>`
        $('#house').html(houseLink);

        $('#section').html(apartmentData.section.name);
        $('#floor').html(apartmentData.floor.name);
    })
    $selectApartment.trigger('change');


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

    $selectMasterType.on('change', function () {
        const status = $selectMasterType.select2('data')[0];
        initSelectStaff(status.id, false);
    })

    $selectMasterType.val(request.masterType).trigger('change');

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

    $selectStatus.val(request.status).trigger('change');

    function initSelectStaff(status, isDisabled) {

        $selectStaff.select2({
            dropdownParent: $('#masterId-wrap'),
            maximumInputLength: 50,
            placeholder: chooseMaster,
            disabled: isDisabled,
            ajax: {
                type: "GET",
                url: '../../system-settings/staff/get-staff',
                data: function (params) {
                    return {
                        name: params.term,
                        page: (params.page - 1) || 0,
                        pageSize: 10,
                        roleName: status
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
        if (request.master) {
            const masterOption = new Option(`${request.master.firstName} ${request.master.lastName}`, request.master.id, true, true);
            $selectStaff.append(masterOption).trigger('change');
        }
    }

    autosize($inputComment);
    autosize($inputDescription);

    $inputComment.val(request.comment);
    $inputDescription.val(request.description);
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
    const val = $inputVisitTime.val().split(':');
    const visitDate = new Date(Date.parse($flatpickrDate.selectedDates));
    visitDate.setHours(val[0]);
    visitDate.setMinutes(val[1]);

    let formData = new FormData($('#master-request-form')[0]);
    formData.set('apartmentOwnerPhone', $('#apartment-owner-phone').html());
    formData.set('visitDate', visitDate.getTime());
    formData.set('id', masterRequestId);
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

$('.button-cancel').on('click', function () {
    restoreInputs(masterRequestToRestore);
});

function restoreInputs(request) {
    const date = new Date(request.visitDate * 1000);
    const apartment = request.apartment;

    $flatpickrDate.setDate(date);
    $flatpickrTime.setDate(date);
    $selectApartmentOwner.val(apartment.owner.id).trigger('change');
    $selectApartment.val(apartment.id).trigger('change');
    $selectMasterType.val(request.master.role.name).trigger('change');
    $selectStatus.val(request.status).trigger('change');
    $selectStaff.val(request.master.id).trigger('change');

    $inputComment.val(request.comment).trigger('change');
    $inputDescription.val(request.description).trigger('change');
}