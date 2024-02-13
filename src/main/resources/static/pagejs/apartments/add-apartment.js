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
        let mapIdRangeNumber = new Map();
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
                            mapIdRangeNumber.set(section.id, section.rangeApartmentNumbers);
                            return {
                                id: section.id,
                                text: section.name
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
            mapIdRangeNumber.forEach((value, key) => {
                console.log(`key: ${key}, value: ${value}`)
                $('.section-select-wrap').find(`option[value="${key}"]`).attr("data-range", value);
            });

            const $label = $('label[for="apartmentNumber"]');
            const rangeNumbers = $(`option[value="${this.value}"]`).attr("data-range");
            $label.html(labelApartmentNumber + ` (${rangeNumbers})`);
            $inputApartmentNumber.removeAttr('disabled');
            $inputApartmentNumber.attr('min', rangeNumbers.split('-')[0]);
            $inputApartmentNumber.attr('max', rangeNumbers.split('-')[1]);
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

    $('[name="personalAccountId"]').select2({
        placeholder: chooseAccount,
        dropdownParent: $('.account-select-wrap')
        // todo add find personal accounts by number
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

    new Cleave('input[name="personalAccountNew"]', {
        delimiter: "-",
        blocks: [5, 5],
    })

    $('input[name="personalAccountNew"]').on('input', function () {
        let val1 = $(this).val();
        $(this).val(val1.replace(/[A-Za-z]/, ''));
    });


    $('#apartmentForm .button-save, #apartmentForm .button-save-and-add').on('click', function () {
        clearAllErrorMessage();
        blockCardDody();
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
                console.log(error)
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