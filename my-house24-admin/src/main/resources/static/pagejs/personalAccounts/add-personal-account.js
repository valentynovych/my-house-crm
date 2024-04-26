$(document).ready(function () {
    initInputAndSelect();
})
const $inputAccountNumber = $('[name="accountNumber"]');
const $selectApartment = $('[name="apartmentId"]');
const $selectHouse = $('[name="houseId"]');
const $selectSection = $('[name="sectionId"]');
const $selectStatus = $('[name="status"]');
const $apartmentOwnerPhone = $('#owner-phone');
const $apartmentOwnerText = $('#apartment-owner');

function initInputAndSelect() {
    $.ajax({
        url: '../personal-accounts/get-minimal-free-account-number',
        type: 'get',
        success: function (response) {
            $inputAccountNumber.val(response);
        },
        error: function (error) {
            console.log(error);
        }
    })

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

    $selectStatus.select2({
        dropdownParent: $selectStatus.parent(),
        placeholder: labelStatus,
        data: [{
            id: 'ACTIVE',
            text: getAccountStatusLabel('ACTIVE')
        }],
        ajax: {
            type: "GET",
            url: '../personal-accounts/get-statuses',
            processResults: function (response) {
                return {
                    results: $.map(response, function (status) {
                        return {
                            id: status,
                            text: getAccountStatusLabel(status)
                        }
                    }),
                };
            }
        }
    });

    $selectHouse.select2({
        placeholder: chooseHouse,
        dropdownParent: $('.house-select-wrap'),
        ajax: {
            type: "GET",
            url: '../houses/get-houses',
            data: function (params) {
                return {
                    name: params.term || '',
                    page: (params.page - 1) || 0,
                    pageSize: 10
                };
            },
            processResults: function (response) {
                return {
                    results: $.map(response.content, function (house) {
                        return {
                            id: house.id,
                            text: house.name
                        }
                    }),
                    pagination: {
                        more: !response.last
                    }
                };
            }
        }
    });

    let houseId;
    $selectHouse.on('change', function () {
        $selectSection.val('').trigger('change');
        $selectApartment.val('').trigger('change');
        $apartmentOwnerText.html(labelNotSet);
        $apartmentOwnerPhone.html(labelNotSet);
        houseId = $(this).val();
        if (houseId > 0) {
            $selectSection.removeAttr('disabled');
            $selectApartment.removeAttr("disabled")
            initHouseNestedSSelects(houseId);
        }
    });

    $selectSection.select2({
        placeholder: chooseSection,
        dropdownParent: $('.section-select-wrap')
    });

    $selectApartment.select2({
        placeholder: chooseFloor,
        dropdownParent: $('.floor-select-wrap')
    });

    function initHouseNestedSSelects(houseId) {
        $selectSection.select2({
            placeholder: chooseSection,
            dropdownParent: $selectSection.parent(),
            ajax: {
                type: "GET",
                url: '../sections/get-sections-by-house/' + houseId,
                data: function (params) {
                    return {
                        name: params.term || '',
                        page: (params.page - 1) || 0,
                        pageSize: 10
                    };
                },
                processResults: function (response) {
                    return {
                        results: $.map(response.content, function (section) {
                            return {
                                id: section.id,
                                text: section.name,
                                range: section.rangeApartmentNumbers
                            }
                        }),
                        pagination: {
                            more: !response.last
                        }
                    };
                }
            }
        });
        initSectionNestedSelect(null, houseId);
    }

    function initSectionNestedSelect(sectionId, houseId) {
        $selectApartment.select2({
            placeholder: chooseSection,
            dropdownParent: $selectApartment.parent(),
            ajax: {
                type: "GET",
                url: '../apartments/get-apartments',
                data: function (params) {
                    let parameters = {
                        apartmentNumber: params.term,
                        house: houseId,
                        page: (params.page - 1) || 0,
                        pageSize: 10
                    }
                    if (sectionId) {
                        parameters.section = sectionId;
                        console.log(parameters);

                    }
                    return parameters;
                },
                processResults: function (response) {
                    return {
                        results: $.map(response.content, function (apartment) {
                            return {
                                id: apartment.id,
                                text: (apartment.apartmentNumber).toString().padStart(5, '00000'),
                                data_name: apartment.owner.fullName,
                                data_phone: apartment.owner.phoneNumber,
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

    $selectSection.on('change', function () {
        const value = $(this).val();
        if (value > 0) {
            initSectionNestedSelect(value, houseId);
        }
    })


    $selectApartment.on('change', function () {
        const select2Element = $selectApartment.select2('data')[0];
        if (select2Element) {
            $apartmentOwnerText.html(select2Element.data_name);
            $apartmentOwnerPhone.html(select2Element.data_phone);
        }
    })

    function decorateAccountNumber(accountNumber) {
        let s = (accountNumber + '').padStart(10, '0000000000');
        return s.substring(0, 5) + '-' + s.substring(5, 10);
    }

    $inputAccountNumber.on('input', function () {
        let val = this.value.replace(/\D/g, '');
        if (Number(val) <= 9999999999) {
            val = ("0000000000" + val).slice(-10);
            $(this).val(decorateAccountNumber(val));
        }
        maxInputLength($(this), 11);
    });


    $('.button-save').on('click', function () {
        clearAllErrorMessage();
        blockCardDody();
        trimInputsValue();

        let formData = new FormData($('#apartmentForm')[0]);


        // for (const formDatum of formData.entries()) {
        //     console.log(formDatum)
        // }

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