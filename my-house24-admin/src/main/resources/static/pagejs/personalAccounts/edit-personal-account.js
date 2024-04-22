const accountId = window.location.pathname.match(/\d+$/);
let accountToRestore;
$(document).ready(function () {
    blockCardDody();
    $.ajax({
        url: '../get-account/' + accountId,
        type: 'get',
        success: function (response) {
            console.log(response);
            accountToRestore = response;
            fillInputs(response);
        },
        error: function (error) {
            console.log(error);
        }
    })
})

const $inputAccountNumber = $('[name="accountNumber"]');
const $selectApartment = $('[name="apartmentId"]');
const $selectHouse = $('[name="houseId"]');
const $selectSection = $('[name="sectionId"]');
const $selectStatus = $('[name="status"]');
const $apartmentOwnerPhone = $('#owner-phone');
const $apartmentOwnerText = $('#apartment-owner');

function fillInputs(account) {
    $inputAccountNumber.val(account.accountNumber);

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
            id: account.status,
            text: getAccountStatusLabel(account.status)
        }],
        ajax: {
            type: "GET",
            url: '../get-statuses',
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
        dropdownParent: $selectHouse.parent(),
        ajax: {
            type: "GET",
            url: '../../houses/get-houses',
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

    $selectApartment.select2({
        placeholder: chooseApartment,
        dropdownParent: $selectApartment.parent(),
    });

    $selectHouse.on('change', function () {
        $selectSection.val('').trigger('change');
        $selectApartment.val('').trigger('change');
        $apartmentOwnerText.html(labelNotSet);
        $apartmentOwnerPhone.html(labelNotSet);
        houseId = $(this).val();
        if (houseId > 0) {
            $selectSection.removeAttr('disabled');
            initHouseNestedSelect(houseId, dataSection);
        }
    });

    $selectSection.on("change", function () {
        $selectApartment.val('').trigger('change');
        sectionId = $(this).val();
        if (sectionId > 0) {
            $selectApartment.removeAttr("disabled")
            initSectionNestedSelect(sectionId, dataApartment);
        }
    });

    $selectApartment.on('change', function () {
        const select2Element = $selectApartment.select2('data')[0];
        if (select2Element) {
            $apartmentOwnerText.html(select2Element.data_name);
            $apartmentOwnerPhone.html(select2Element.data_phone);
        }
    })

    const isHaveApartment = !!account.apartment;
    let dataHouse = null;
    let dataSection = null;
    let dataApartment = null;
    let houseId = '';
    let sectionId = '';
    if (isHaveApartment) {
        $selectSection.removeAttr('disabled')
        $selectApartment.removeAttr('disabled')
        const apartment = account.apartment;
        houseId = apartment.house.id;
        dataHouse = {
            id: apartment.house.id,
            text: apartment.house.name
        }
        const houseOption = new Option(dataHouse.text, dataHouse.id, true, true);
        $selectHouse.append(houseOption);
        $selectHouse.val(dataHouse.id).trigger('change');

        sectionId = apartment.section.id;
        dataSection = {
            id: apartment.section.id,
            text: apartment.section.name,
        }
        const sectionOption = new Option(dataSection.text, dataSection.id, true, true);
        $selectSection.append(sectionOption);
        $selectSection.val(dataSection.id).trigger('change');

        dataApartment = {
            id: apartment.id,
            text: (apartment.apartmentNumber).toString().padStart(5, '00000'),
        }
        const apartmentOption = new Option(dataApartment.text, dataApartment.id, true, true);
        $selectApartment.append(apartmentOption);
        $selectApartment.val(dataApartment.id).trigger('change');

        $apartmentOwnerText.html(apartment.owner.fullName);
        $apartmentOwnerPhone.html(apartment.owner.phoneNumber);
    }

    function initHouseNestedSelect(houseId) {
        $selectSection.select2({
            placeholder: chooseSection,
            dropdownParent: $selectSection.parent(),
            ajax: {
                type: "GET",
                url: '../../sections/get-sections-by-house/' + houseId,
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
    }

    function initSectionNestedSelect(sectionId) {

        $selectApartment.select2({
            placeholder: chooseApartment,
            dropdownParent: $selectApartment.parent(),
            ajax: {
                type: "GET",
                url: '../../apartments/get-apartments?section=' + sectionId,
                data: function (params) {
                    return {
                        apartmentNumber: params.term,
                        page: (params.page - 1) || 0,
                        pageSize: 10
                    };
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

        let formData = new FormData($('#apartmentForm')[0]);

        let accountNumber = formData.get('accountNumber');
        if (accountNumber) {
            formData.set('accountNumber', accountNumber.replace(/\D/, ''));
        }
        formData.set('id', accountToRestore.id);

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

    $('.button-cancel').on('click', () => fillInputs(accountToRestore))
}