const sheetId = window.location.pathname.match(/\d+/);
const $inputSheetNumber = $('[name="sheetNumber"]');
const $inputCreationDate = $('[name="creationDate"]');
const $selectPaymentItem = $('[name="paymentItemId"]');
const $inputAmount = $('[name="amount"]');
const $inputComment = $('[name="comment"]');
const $checkboxIsProcessed = $('[name="isProcessed"]');
const $selectStaff = $('[name="staffId"]');
let sheetToRestore;

$(document).ready(function () {
    blockCardDody();
    $.ajax({
        url: '../get-sheet/' + sheetId,
        type: 'get',
        success: function (response) {
            sheetToRestore = response;
            fillInputs(response);
        },
        error: function (error) {
            toastr.error(errorMessage);
        }
    })
})

function fillInputs(sheet) {
    $inputSheetNumber.val(sheet.sheetNumber);

    const flatpickrDate = flatpickr($inputCreationDate, {
        dateFormat: "d.m.Y",
    });
    flatpickrDate.setDate(new Date(sheet.creationDate * 1000));

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
                    paymentType: "EXPENSE",
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

        let formData = new FormData($('#expense-sheet-form')[0]);
        formData.set("processed", $checkboxIsProcessed.prop('checked'));
        formData.set("sheetNumber", $inputSheetNumber.val())
        formData.set("id", sheetId)
        formData.set("amount", $inputAmountCleave.getRawValue())
        formData.delete("isProcessed")

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