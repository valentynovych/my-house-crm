$(document).ready(function () {
    initInputAndSelect();
})
const $inputSheetNumber = $('[name="sheetNumber"]');
const $inputCreationDate = $('[name="creationDate"]');
const $selectPaymentItem = $('[name="paymentItemId"]');
const $inputAmount = $('[name="amount"]');
const $inputComment = $('[name="comment"]');
const $checkboxIsProcessed = $('[name="isProcessed"]');
const $selectStaff = $('[name="staffId"]');

function initInputAndSelect() {

    $.ajax({
        url: '../cash-register/get-next-sheet-number',
        type: 'get',
        success: function (response) {
            $inputSheetNumber.val(response);
        },
        error: function (error) {
            console.log(error);
        }
    })

    $inputCreationDate.flatpickr({
        dateFormat: "d.m.Y",
        defaultDate: new Date()
    })

    $selectPaymentItem.select2({
        dropdownParent: $('.paymentItemId-select-wrap'),
        maximumInputLength: 50,
        placeholder: choosePaymentItem,
        ajax: {
            type: "GET",
            url: '../system-settings/payment-items/get-items',
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

    $selectStaff.select2({
        dropdownParent: $('.staffId-select-wrap'),
        placeholder: chooseStaff,
        maximumInputLength: 50,
        ajax: {
            type: "GET",
            url: '../system-settings/staff/get-staff',
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

    const $inputAmountCleave = new Cleave($inputAmount, {
        numeral: true,
        numeralThousandsGroupStyle: "thousand"
    });

    autosize($inputComment);

    $('.button-save').on('click', function () {
        clearAllErrorMessage();
        blockCardDody();

        let formData = new FormData($('#expense-sheet-form')[0]);
        formData.set("processed", $checkboxIsProcessed.prop('checked'));
        formData.set("sheetNumber", $inputSheetNumber.val());
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

    $('.button-cancel').on('click', () => window.history.back())
}