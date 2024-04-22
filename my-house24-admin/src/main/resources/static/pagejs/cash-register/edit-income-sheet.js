const sheetId = window.location.pathname.match(/\d+$/);
const $inputSheetNumber = $('[name="sheetNumber"]');
const $inputCreationDate = $('[name="creationDate"]');
const $selectOwner = $('[name="ownerId"]');
const $selectPersonalAccount = $('[name="personalAccountId"]');
const $selectPaymentItem = $('[name="paymentItemId"]');
const $inputAmount = $('[name="amount"]');
const $inputComment = $('[name="comment"]');
const $checkboxIsProcessed = $('[name="isProcessed"]');
const $selectStaff = $('[name="staffId"]');
let sheetToRestore;

$inputSheetNumber.prop('disabled', true);
$inputSheetNumber.on('click', function () {
    $(this).prop('disabled', true);})

$(document).ready(function () {
    blockCardDody();
    $.ajax({
        url: '../get-sheet/' + sheetId,
        type: 'get',
        success: function (response) {
            console.log(response);
            sheetToRestore = response;
            fillInputs(response);
        },
        error: function (error) {
            console.log(error);
        }
    })
})

function fillInputs(sheet) {
    $inputSheetNumber.val(sheet.sheetNumber);

    const flatpickrDate = flatpickr($inputCreationDate, {
        dateFormat: "d.m.Y",
    });
    flatpickrDate.setDate(new Date(sheet.creationDate * 1000));

    const personalAccount = sheet.personalAccount;
    $selectOwner.select2({
        debug: true,
        dropdownParent: $('.ownerId-select-wrap'),
        maximumInputLength: 50,
        placeholder: chooseOwner,
        data: [{
            id: personalAccount.apartmentOwner.id,
            text: personalAccount.apartmentOwner.fullName
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
                            text: owner.fullName
                        }
                    }),
                    pagination: {
                        more: !response.last
                    }
                };
            }
        }
    });
    let ownerId;
    initSelectPersonalAccount(ownerId, false, personalAccount);

    $selectOwner.on('change', function () {
        ownerId = this.value;
        $selectPersonalAccount.val('').trigger("change");
        if (ownerId) {
            initSelectPersonalAccount(ownerId, false, '');
        } else {
            initSelectPersonalAccount(false, true, '');
        }
    })

    function initSelectPersonalAccount(ownerId, isDisabled, personalAccount) {
        $selectPersonalAccount.select2({
            dropdownParent: $('.personalAccountId-select-wrap'),
            maximumInputLength: 50,
            placeholder: choosePersonalAccount,
            disabled: isDisabled,

            ajax: {
                type: "GET",
                url: `../../personal-accounts/get-personal-accounts${ownerId ? '?owner=' + ownerId : ''}`,
                data: function (params) {
                    return {
                        accountNumber: params.term,
                        page: (params.page - 1) || 0,
                        pageSize: 10
                    };
                },
                processResults: function (response) {
                    console.log(response)
                    return {
                        results: $.map(response.content, function (account) {
                            return {
                                id: account.id,
                                text: account.accountNumber
                            }
                        }),
                        pagination: {
                            more: !response.last
                        }
                    };
                }
            }
        });
        if (personalAccount) {
            $selectPersonalAccount.select2({
                data: [{
                    id: personalAccount.id,
                    text: personalAccount.accountNumber
                }]
            })
        }
    }


    $selectPaymentItem.select2({
        dropdownParent: $('.paymentItemId-select-wrap'),
        maximumInputLength: 50,
        placeholder: choosePaymentItem,
        data: [{
            id: sheet.paymentItem.id,
            text: sheet.paymentItem.name
        }],
        ajax: {
            type: "GET",
            url: '../../system-settings/payment-items/get-items',
            data: function (params) {
                return {
                    paymentType: "INCOME",
                    name: params.term,
                    page: (params.page - 1) || 0,
                    pageSize: 10
                };
            },
            processResults: function (response) {
                return {
                    results: $.map(response.content, function (item) {
                        return {
                            id: item.id,
                            text: item.name
                        }
                    }),
                    pagination: {
                        more: !response.last
                    }
                };
            }
        }
    });

    const $inputAmountCleave = new Cleave($inputAmount, {
        numeral: true,
        numeralThousandsGroupStyle: "thousand"
    });

    $inputAmountCleave.setRawValue(sheet.amount);
    $checkboxIsProcessed.prop('checked', sheet.processed)

    $selectStaff.select2({
        dropdownParent: $('.staffId-select-wrap'),
        placeholder: chooseStaff,
        maximumInputLength: 50,
        data: [{
            id: sheet.staff.id,
            text: `${sheet.staff.firstName} ${sheet.staff.lastName}`,
        }],
        ajax: {
            type: "GET",
            url: '../../system-settings/staff/get-staff',
            data: function (params) {
                return {
                    name: params.term,
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

    autosize($inputComment);
    $inputComment.val(sheet.comment);

    $('.button-save').on('click', function () {
        clearAllErrorMessage();
        blockCardDody();

        let formData = new FormData($('#income-sheet-form')[0]);
        formData.set("processed", $checkboxIsProcessed.prop('checked'));
        formData.set("sheetNumber", $inputSheetNumber.val())
        formData.set("id", sheetId)
        formData.set("amount", $inputAmountCleave.getRawValue())

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

    $('.button-cancel').on('click', () => fillInputs(sheetToRestore));
}