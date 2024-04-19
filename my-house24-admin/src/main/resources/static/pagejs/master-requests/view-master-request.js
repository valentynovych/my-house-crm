const masterRequestId = window.location.pathname.match(/\d+$/);
let sheetType;

blockCardDody();
$(document).ready(function () {
    $.ajax({
        url: '../get-request/' + masterRequestId,
        type: 'get',
        success: function (response) {
            console.log(response)
            fillInputs(response);
        },
        error: function (error) {
            toastr.error(errorMessage);
        }
    })
});

function fillInputs(request) {
    const date = new Date(request.visitDate * 1000);
    const $flatpickrDate = flatpickr('#visitDate', {
        dateFormat: "d.m.Y",
        defaultDate: date
    });
    const $flatpickrTime = flatpickr('#visitTime', {
        enableTime: true,
        noCalendar: true,
        minuteIncrement: 10,
        time_24hr: true,
        minTime: '9:30',
        maxTime: '21:30',
        defaultDate: date
    });


    const $breadcrumb = $('[data-name="request-title"]')
    const $edit = $('#edit-link');

    $breadcrumb.html($breadcrumb.html() + `<span>${masterRequestId}</span>`);
    $edit.attr('href', $edit.attr('href') + `edit-request/${masterRequestId}`);

    const statusBadge = function (statusValue) {
        switch (statusValue) {
            case 'NEW':
                return `<span class="badge rounded-pill bg-primary">${getMasterRequestStatus(statusValue)}</span>`;
            case 'IN_PROGRESS':
                return `<span class="badge rounded-pill bg-info">${getMasterRequestStatus(statusValue)}</span>`;
            case 'DONE':
                return `<span class="badge rounded-pill bg-success">${getMasterRequestStatus(statusValue)}</span>`;
            case 'CANCELED':
                return `<span class="badge rounded-pill bg-danger">${getMasterRequestStatus(statusValue)}</span>`;
        }
    }

    const apartment = request.apartment;
    const masterName = request.master ? `${request.master.lastName} ${request.master.firstName}` : '---';
    $('#status').html(statusBadge(request.status));
    $('#apartmentOwner').html(apartment.owner.fullName)
    $('#apartmentOwnerPhone').html(apartment.owner.phoneNumber)
    $('#apartment').html(`${(apartment.apartmentNumber + '').padStart(3, '000')} ${apartment.house.name}`)
    $('#masterType').html(getMasterTypeLabel(request.masterType))
    $('#master').html(`${masterName}`)
    $('#description').html(request.description);
    $('#comment').html(request.comment);
    let localeString = new Date(request.creationDate * 1000).toLocaleString('uk-UA');
    $('#creationDate').html(localeString.substring(0, localeString.length - 3));
}

function getMasterTypeLabel(status) {
    switch (status) {
        case 'ELECTRICIAN':
            return roleElectrician;
        case 'PLUMBER':
            return rolePlumber;
        default:
            return '- - - -'
    }
}

function getMasterRequestStatus(sheetType) {
    switch (sheetType) {
        case "NEW":
            return labelStatusNew;
        case "IN_PROGRESS":
            return labelStatusInProgress
        case "DONE":
            return labelStatusDone
        case "CANCELED":
            return labelStatusCanceled;
        default:
            return '- - - -'
    }
}