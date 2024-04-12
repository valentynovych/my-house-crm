const personalAccountId = window.location.pathname.match(/\d+$/);
let accountToRestore;

blockCardDody();
$(document).ready(function () {
    $.ajax({
        url: '../get-account/' + personalAccountId,
        type: 'get',
        success: function (response) {
            console.log(response)
            accountToRestore = response;
            fillInputs(response);
        },
        error: function (error) {
            toastr.error(errorMessage);
        }
    })
});

function getAccountStatusLabel(status) {
    switch (status) {
        case 'ACTIVE':
            return accountStatusActive;
        case 'NONACTIVE' :
            return accountStatusNonActive;
        default:
            return '- - - -'
    }
}

function fillInputs(account) {
    const status = getAccountStatusLabel(account.status);
    const statusBadge = account.status === 'ACTIVE'
        ? `<span class="badge rounded-pill bg-success">${status}</span>`
        : account.status === 'NONACTIVE'
            ? `<span class="badge rounded-pill bg-danger">${status}</span>`
            : `<span class="badge rounded-pill bg-dark">${status}</span>`;

    const $curr = $('#curr-apartment-title');
    const $breadcrumb = $('.breadcrumb-item.active');
    const $edit = $('#edit-link');
    const $decorateAccountNumber = decorateAccountNumber(account.accountNumber);
    $breadcrumb.addClass('d-flex flex-wrap gap-2')
    $breadcrumb.html($breadcrumb.html() + `<span>№${$decorateAccountNumber}</span>`)
    $edit.attr('href', $edit.attr('href') + account.id);

    let balanceText = '-';
    let apartment = '-';
    const isHaveApartment = !!account.apartment;
    if (isHaveApartment) {
        apartment = account.apartment;
        const numberFormat = new Intl.NumberFormat('uk');
        const balance = apartment.balance;
        balanceText = balance > 0
            ? `<span class="text-success">${numberFormat.format(balance)}</span>`
            : balance < 0
                ? `<span class="text-danger">${numberFormat.format(balance)}</span>`
                : `<span class="text-dark">${numberFormat.format(balance)}</span>`;
    }

    $('#status').html(statusBadge);
    $('#accountNumber').val($decorateAccountNumber);


    const $house = $('#house');
    const $section = $('#section');
    const $apartment = $('#apartment');
    const $owner = $('#owner');
    const $balance = $('#balance');

    if (isHaveApartment) {
        const apartmentNumber = (apartment.apartmentNumber).toString().padStart(5, '00000');

        $house.html(`<a href="../../houses/veiw-house/${apartment.house.id}">${apartment.house.name}</a>`);
        $section.html(apartment.section.name);
        $apartment.html(`<a href="../../apartments/view-apartment/${apartment.id}">${apartmentNumber}</a>`);
        $owner.html(`<a href="../../owners/view-owner/${apartment.owner.id}">${apartment.owner.fullName}</a>`);
        $balance.html(`${balanceText}`);
    } else {
        $house.html(`${labelNotSet}`);
        $section.html(`${labelNotSet}`);
        $apartment.html(`${labelNotSet}`);
        $owner.html(`${labelNotSet}`);
        $balance.html('-');
    }

    const $cardFooter = $('.card-footer');
    $cardFooter.addClass('d-flex flex-column gap-2')
    $(`<a href="../../meter-readings/apartment/${account.id}">${labelLinkViewMeters}</a>`).appendTo($cardFooter);
    $(`<a href="../../cash-register?personalAccount=${account.id}&sheetType=INCOME">${labelLinkViewIncomes}</a>`).appendTo($cardFooter);
    $(`<a href="../../invoices?apartment=${account.apartment.apartmentNumber}">${labelLinkViewInvoices}</a>`).appendTo($cardFooter);
}

$('#accept-payment').on('click', function () {
    window.location = '../../cash-register/add-income-sheet?forAccount=' + personalAccountId;
});

$('#create-invoice').on('click', function () {
    window.location = '../../invoices/add?forApartment=' + accountToRestore.apartment.id;
});

function decorateAccountNumber(accountNumber) {
    let s = (accountNumber + '').padStart(10, '0000000000');
    return s.substring(0, 5) + '-' + s.substring(5, 10);
}