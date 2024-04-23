$(document).ready(function () {
    initInputAndSelect();
})
const $inputSheetNumber = $('[name="sheetNumber"]');
const $inputCreationDate = $('[name="creationDate"]');
const $selectOwner = $('[name="ownerId"]');
const $selectPersonalAccount = $('[name="personalAccountId"]');
const $selectPaymentItem = $('[name="paymentItemId"]');
const $inputAmount = $('[name="amount"]');
const $inputComment = $('[name="comment"]');
const $checkboxIsProcessed = $('[name="isProcessed"]');
const $selectStaff = $('[name="staffId"]');

const $inputAmountCleave = new Cleave($inputAmount, {
    numeral: true,
    numeralThousandsGroupStyle: "thousand"
});

$inputSheetNumber.prop('disabled', true);

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

    $selectOwner.select2({
        debug: true,
        dropdownParent: $('.ownerId-select-wrap'),
        maximumInputLength: 50,
        placeholder: chooseOwner,
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
    initSelectPersonalAccount(ownerId, true);

    $selectOwner.on('change', function () {
        ownerId = this.value;
        console.log(ownerId);
        if (ownerId) {
            initSelectPersonalAccount(ownerId, false);
        } else {
            initSelectPersonalAccount(false, true);
        }
    })

    function initSelectPersonalAccount(ownerId, isDisabled) {
        $selectPersonalAccount.select2({
            dropdownParent: $('.personalAccountId-select-wrap'),
            maximumInputLength: 50,
            placeholder: choosePersonalAccount,
            disabled: isDisabled,
            ajax: {
                type: "GET",
                url: `../personal-accounts/get-personal-accounts${ownerId ? '?owner=' + ownerId : ''}`,
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
    }


    $selectPaymentItem.select2({
        dropdownParent: $('.paymentItemId-select-wrap'),
        maximumInputLength: 50,
        placeholder: choosePaymentItem,
        ajax: {
            type: "GET",
            url: '../system-settings/payment-items/get-items',
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
    applyRequestParameters();
}

$('.button-save').on('click', function () {
    clearAllErrorMessage();
    blockCardDody();
    trimInputsValue();

    let formData = new FormData($('#income-sheet-form')[0]);
    formData.set("processed", $checkboxIsProcessed.prop('checked'));
    formData.set("sheetNumber", $inputSheetNumber.val())
    formData.set("amount", $inputAmountCleave.getRawValue())
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

function applyRequestParameters() {
    let sheetId = findGetParameter('copyFrom');
    if (sheetId) {
        copySheetFrom(sheetId);
    }
    let personalAccountId = findGetParameter('forAccount');
    if (personalAccountId) {
        createSheetForAccount(personalAccountId);
    }

}

function copySheetFrom(sheetId) {

    blockBy('#income-sheet-form');

    $.ajax({
        url: 'get-sheet/' + sheetId,
        type: 'get',
        success: function (response) {
            fillInputFromCopy(response);
        },
        error: function (error) {
            console.log(error);
            toastr.error(errorMessage);
        }
    })
}

function fillInputFromCopy(copySheet) {
    const apartmentOwnerOption = new Option(
        copySheet.personalAccount.apartmentOwner.fullName,
        copySheet.personalAccount.apartmentOwner.id,
        true, true);
    $selectOwner.append(apartmentOwnerOption).trigger('change');

    const personalAccountOption = new Option(
        copySheet.personalAccount.accountNumber,
        copySheet.personalAccount.id,
        true, true);
    $selectPersonalAccount.append(personalAccountOption).trigger('change');

    const paymentItemOption = new Option(copySheet.paymentItem.name, copySheet.paymentItem.id, true, true);
    $selectPaymentItem.append(paymentItemOption).trigger('change');

    $inputAmountCleave.setRawValue(copySheet.amount);
    $inputComment.val(copySheet.comment);
    $checkboxIsProcessed.prop('checked', copySheet.processed);

    const staffOption = new Option(`${copySheet.staff.firstName} ${copySheet.staff.lastName}`, copySheet.staff.id, true, true);
    $selectStaff.append(staffOption).trigger('change');

    unblockBy('#income-sheet-form');
}

function findGetParameter(parameterName) {
    let result = null,
        tmp = [];
    location.search
        .substr(1)
        .split("&")
        .forEach(function (item) {
            tmp = item.split("=");
            if (tmp[0] === parameterName) result = decodeURIComponent(tmp[1]);
        });
    return result;
}

function createSheetForAccount(personalAccountId) {
    blockBy("#income-sheet-form");
    $.ajax({
        url: '../personal-accounts/get-account/' + personalAccountId,
        type: 'get',
        success: function (response) {
            fillNestedAccountFields(response);
        },
        error: function (error) {
            console.log(error);
            toastr.error(errorMessage);
        }
    })
}

function fillNestedAccountFields(personalAccount) {
    console.log(personalAccount);
    const apartmentOwner = personalAccount.apartment.owner;
    const apartmentOwnerOption = new Option(apartmentOwner.fullName, apartmentOwner.id, true, true);
    $selectOwner.append(apartmentOwnerOption).trigger('change');

    const personalAccountOption =
        new Option(personalAccount.accountNumber, personalAccount.id, true, true);
    $selectPersonalAccount.append(personalAccountOption).trigger('change');

    unblockBy("#income-sheet-form");
}