$(document).ready(function () {
    initInputAndSelect();
})
const $inputVisitDate = $('[name="visitDate"]');
const $inputVisitTime = $('[name="visitTime"]');
const $inputDescription = $('[name="description"]');
const $selectApartment = $('[name="apartmentId"]');
const $selectMasterType = $('[name="masterType"]');
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
});
$flatpickrTime.setDate(date);

function initInputAndSelect() {

    $selectApartment.select2({
        dropdownParent: $('#apartmentId-wrap'),
        minimumResultsForSearch: -1,
        placeholder: chooseApartment,
        ajax: {
            type: "GET",
            url: `../apartments/get-owner-apartments`,
            data: function (params) {
                return {
                    page: (params.page - 1) || 0,
                    pageSize: 10,
                };
            },
            processResults: function (response) {
                console.log(response)
                return {
                    results: $.map(response.content, function (apartment) {
                        return {
                            id: apartment.id,
                            text: (apartment.apartmentNumber).padStart(5, '00000'),
                        }
                    }),
                    pagination: {
                        more: !response.last
                    }
                };
            }
        }
    });


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

    autosize($inputDescription);
}

function getRoleLabel(role) {
    switch (role) {
        case 'ELECTRICIAN':
            return roleElectrician;
        case 'PLUMBER':
            return rolePlumber;
    }
}

$('.button-save').on('click', function () {
    clearAllErrorMessage();
    blockCardDody();
    const val = $inputVisitTime.val().split(':');
    const visitDate = new Date(Date.parse($flatpickrDate.selectedDates));
    visitDate.setHours(+val[0]);
    visitDate.setMinutes(+val[1]);

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