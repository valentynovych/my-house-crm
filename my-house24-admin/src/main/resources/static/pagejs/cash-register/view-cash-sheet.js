const sheetId = window.location.pathname.match(/\d+$/);
let sheetType;

blockCardDody();
$(document).ready(function () {
    $.ajax({
        url: '../get-sheet/' + sheetId,
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

function fillInputs(sheet) {
    sheetType = sheet.sheetType;
    const $breadcrumb = $('.breadcrumb-item.active');
    const $edit = $('#edit-link');

    $breadcrumb.addClass('d-flex flex-wrap gap-2');
    $breadcrumb.html($breadcrumb.html() + `<span>№${sheet.sheetNumber}</span>`);
    $edit.attr('href',
        $edit.attr('href') + (sheetType === 'INCOME'
            ? 'edit-income-sheet/'
            : 'edit-expense-sheet/') + sheetId);

    $('#sheetNumber').val(sheet.sheetNumber);
    $('#creationDate').val(new Date(sheet.creationDate * 1000).toLocaleDateString())

    const personalAccount = sheet.personalAccount;
    const invoice = sheet.invoice;
    $('#owner').html(personalAccount ? personalAccount.apartmentOwner.fullName : '-');
    $('#personalAccount').html(personalAccount ? `<a href="../../personal-accounts/view-account/${personalAccount.id}">${decorateAccountNumber(personalAccount.accountNumber)}</a>` : '-');
    $('#paymentItem').html(sheet.paymentItem.name);
    $('#invoice').html(invoice ? `<a href="../../invoices/view-invoice/${invoice.id}">${invoice.number} ${dividerFrom} ${new Date(invoice.creationDate * 1000).toLocaleDateString()} </a>` : '-');

    const staff = sheet.staff;
    $('#staff').html(`<a href="../../system-settings/staff/view-staff/${staff.id}">${staff.firstName} ${staff.lastName}</a>`)

    const numberFormat = new Intl.NumberFormat('uk');
    const balance = sheet.amount;
    let balanceText = sheet.sheetType === 'INCOME'
        ? `<span class="text-success">${numberFormat.format(balance)}</span>`
        : `<span class="text-danger">${numberFormat.format(balance)}</span>`;
    $('#amount').html(balanceText);
    $('#comment').html(sheet.comment);
}

$('#export-to-excel').on('click', function () {
    const url = `../export-view-to-exel/${sheetId}`;
    blockBy('#export-to-excel')
    $.ajax({
        type: 'get',
        url: url,
        success: function (result) {
            const a = document.createElement('a');
            a.href = url;
            a.target = '_blank';
            a.download = true;
            a.click();
            unblockBy('#export-to-excel');
        },
        error: function () {
            toastr.error(errorMessage)
        }
    })

})

$('#copy-sheet').on('click', function () {
    window.location = (sheetType === 'INCOME'
        ? '../add-income-sheet'
        : '../add-expense-sheet') + '?copyFrom=' + sheetId;
});

$('.submit-delete').on('click', function () {
    $.ajax({
        url: '../delete-sheet/' + sheetId,
        type: 'delete',
        success: function (response) {
            toastr.success(successMessageOnDelete);
            setTimeout(() => window.history.back(), 500);
        }, error: function (error) {
            console.log(error);
            $('.close-modal').click();
            if (error.status === 423) {
                toastr.error(errorMessageOnDelete);
            } else {
                toastr.error(errorMessage);
            }
        }
    });
});

function decorateAccountNumber(accountNumber) {
    let s = (accountNumber + '').padStart(10, '0000000000');
    return s.substring(0, 5) + '-' + s.substring(5, 10);
}