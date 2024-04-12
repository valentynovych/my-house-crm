const apartmentId = window.location.pathname.match(/\d+$/);
let apartmentToRestore;

blockCardDody();
$(document).ready(function () {
    $.ajax({
        url: '../get-apartment/' + apartmentId,
        type: 'get',
        success: function (response) {
            console.log(response)
            apartmentToRestore = response;
            fillInputs(response);
        },
        error: function (error) {
            toastr.error(errorMessage);
        }
    })
});

function fillInputs(apartment) {
    const apartmentNumber = (apartment.apartmentNumber + '').padStart(5, '00000');
    const houseName = apartment.house.name;
    const title = ` №${apartmentNumber}, ${houseName}`

    const $curr = $('#curr-apartment-title');
    const $breadcrumb = $('.breadcrumb-item.active span');
    const $edit = $('#edit-link');
    $breadcrumb.html($breadcrumb.html() + title)
    $curr.html($curr.html() + title);
    $edit.attr('href', $edit.attr('href') + apartment.id);

    $('#personal-account').html(`<a href="../../personal-accounts/veiw-account/${apartment.personalAccount.id}">
                                            ${decorateAccountNumber(apartment.personalAccount.accountNumber)}</a>`);
    $('#apartment-number').html(apartmentNumber);
    $('#area').html(apartment.area);

    $('#house').html(`<a href="../../houses/view-house/${apartment.house.id}">${houseName}</a>`);
    $('#section').html(apartment.section.name);
    $('#floor').html(apartment.floor.name);
    $('#owner').html(`<a href="../../owners/view-owner/${apartment.owner.id}">${apartment.owner.fullName}</a>`);
    $('#tariff').html(`<a href="../../tariffs/view-tariff/${apartment.tariff.id}">${apartment.tariff.name}</a>`);

    const $cardFooter = $('.card-footer');
    $cardFooter.addClass('d-flex flex-column gap-2')
    $(`<a href="../../meter-readings/apartment/${apartment.id}">${labelLinkViewMeters}</a>`).appendTo($cardFooter);
    $(`<a href="../../cash-register?personalAccount=${apartment.personalAccount.id}&sheetType=INCOME">${labelLinkViewIncomes}</a>`).appendTo($cardFooter);
    $(`<a href="../../invoices?apartment=${apartment.apartmentNumber}">${labelLinkViewInvoices}</a>`).appendTo($cardFooter);
}

$('#accept-payment').on('click', function () {
    window.location = '../../cash-register/add-income-sheet?forAccount=' + apartmentToRestore.personalAccount.id;
});

$('#create-invoice').on('click', function () {
    window.location = '../../invoices/add?forApartment=' + apartmentToRestore.id;
});

function decorateAccountNumber(accountNumber) {
    let s = (accountNumber + '').padStart(10, '0000000000');
    return s.substring(0, 5) + '-' + s.substring(5, 10);
}