$(document).ready(function () {
    initInputAndSelect();
})
const $inputApartmentNumber = $('[name="apartmentNumber"]');
const $selectHouse = $('[name="houseId"]');
const $sectionSelect = $('[name="sectionId"]');
const $floorSelect = $('[name="floorId"]');
const $ownerSelect = $('[name="ownerId"]');
const $tariffSelect = $('[name="tariffId"]');

function initInputAndSelect() {

    $inputApartmentNumber.on('input', function () {
            let val = this.value;
            if (Number(val) <= 99999) {
                val = ("0000" + val).slice(-5);
                $(this).val(val);
            }
            maxInputLength($(this), 5);
        }
    );


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

    $selectHouse.on('change', function () {
        $sectionSelect.val('').trigger('change');
        $floorSelect.val('').trigger('change');
        const houseId = $(this).val();
        if (houseId > 0) {
            $sectionSelect.removeAttr('disabled');
            $floorSelect.removeAttr("disabled")
            initHouseNestedSSelects(houseId);
        }
    });

    $sectionSelect.select2({
        placeholder: chooseSection,
        dropdownParent: $('.section-select-wrap')
    });

    $floorSelect.select2({
        placeholder: chooseFloor,
        dropdownParent: $('.floor-select-wrap')
    });

    function initHouseNestedSSelects(houseId) {
        $sectionSelect.empty();
        $sectionSelect.select2({
            placeholder: chooseSection,
            dropdownParent: $('.section-select-wrap'),
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
                                rangeApartmentNumbers: section.rangeApartmentNumbers
                            }
                        }),
                        pagination: {
                            more: !response.last
                        }
                    };
                }
            }
        });

        $sectionSelect.on("select2:select", function () {
            const apartmentRangeNumbers = $sectionSelect.select2("data")[0].rangeApartmentNumbers;
            const $label = $('label[for="apartmentNumber"]');
            
            $label.html(labelApartmentNumber + ` (${apartmentRangeNumbers})`);
            $inputApartmentNumber.removeAttr('disabled');
            $inputApartmentNumber.attr('min', apartmentRangeNumbers.split('-')[0]);
            $inputApartmentNumber.attr('max', apartmentRangeNumbers.split('-')[1]);
        });

        $floorSelect.select2({
            placeholder: chooseSection,
            dropdownParent: $('.floor-select-wrap'),
            ajax: {
                type: "GET",
                url: '../floors/get-floors-by-house/' + houseId,
                data: function (params) {
                    return {
                        name: params.term || '',
                        page: (params.page - 1) || 0,
                        pageSize: 10
                    };
                },
                processResults: function (response) {
                    return {
                        results: $.map(response.content, function (floor) {
                            return {
                                id: floor.id,
                                text: floor.name
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


    $ownerSelect.select2({
        placeholder: chooseOwner,
        dropdownParent: $('.owner-select-wrap'),
        ajax: {
            type: "GET",
            url: '../owners/get-owners',
            data: function (params) {
                return {
                    fullName: params.term || '',
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


    $tariffSelect.select2({
        placeholder: chooseTariff,
        dropdownParent: $('.tariff-select-wrap'),
        ajax: {
            type: "GET",
            url: '../system-settings/tariffs/get-tariffs',
            data: function (params) {
                return {
                    page: (params.page - 1) || 0,
                    pageSize: 10
                };
            },
            processResults: function (response) {
                return {
                    results: $.map(response.content, function (tariff) {
                        return {
                            id: tariff.id,
                            text: tariff.name
                        }
                    }),
                    pagination: {
                        more: !response.last
                    }
                };
            }
        }
    });

    const $personalAccountSelect = $('[name="personalAccountId"]');
    $personalAccountSelect.select2({
        placeholder: chooseAccount,
        dropdownParent: $('.account-select-wrap'),
        allowClear: true,
        ajax: {
            type: "GET",
            url: '../personal-accounts/get-free-accounts-find-number',
            data: function (params) {
                return {
                    accountNumber: params.term || '',
                    page: (params.page - 1) || 0,
                    pageSize: 10
                };
            },
            processResults: function (response) {
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

    const inputArea = new Cleave(`[name="area"]`, {
        numeral: true,
        numeralThousandsGroupStyle: "thousand",
        onValueChanged: function () {
            const maxSize = 6;
            const rawValue = inputArea.getRawValue();
            if (rawValue.length > maxSize || Number(rawValue) > 500) {
                inputArea.setRawValue(rawValue.substring(0, rawValue.length - 1));
            }
        },
    });

    const $personalAccountNew = $('input[name="personalAccountNew"]');

    $personalAccountNew.on('input', function () {
        let val = this.value.replace(/\D/g, '');
        if (Number(val) <= 9999999999) {
            val = ("0000000000" + val).slice(-10);
            $(this).val(val.substring(0, 5) + '-' + val.substring(5, 10));
        }
        maxInputLength($(this), 11);
    });

    $personalAccountNew.on('change', function () {
        let value = this.value;
        if (value == 0 || value === '00000-00000') {
            $personalAccountSelect.removeAttr('disabled');
        } else {
            $personalAccountSelect.select2('enable', false);
        }
    });

    $personalAccountSelect.on('change', function () {
        let value = this.value;
        if (value.length > 0) {
            $personalAccountNew.attr('disabled', 'disabled');
        } else {
            $personalAccountNew.removeAttr('disabled');
        }
    });


    $('#apartmentForm .button-save, #apartmentForm .button-save-and-add').on('click', function () {
        clearAllErrorMessage();
        blockCardDody();
        trimInputsValue();
        $('button.bg-label-danger').removeClass('bg-label-danger');

        let formData = new FormData($('#apartmentForm')[0]);

        // for (const formDatum of formData.entries()) {
        //     console.log(formDatum)
        // }
        const mustReset = $(this).hasClass('button-save-and-add');
        $.ajax({
            type: 'post',
            url: '',
            processData: false,
            contentType: false,
            data: formData,
            success: function (response) {
                if (mustReset) {
                    toastr.success(successSaveMessage);
                    resetForm();
                } else {
                    window.history.back();
                }
            },
            error: function (error) {
                printErrorMessageToField(error);
                toastr.error(errorMessage)
            }
        });
    });

    function resetForm() {
        $($selectHouse, $sectionSelect, $floorSelect, $ownerSelect, $tariffSelect)
            .val(null).trigger('change');
        $('#apartmentForm')[0].reset();
    }

    $('.button-cancel').on('click', () => window.history.back())
}