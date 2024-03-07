const $inputSubject = $("#subject");
let $editorTextMessage;
const $checkboxForArrears = $("#forArrears");
const $selectHouse = $("#house");
const $selectSection = $("#section");
const $selectFloor = $("#floor");
const $selectApartment = $("#apartment");
$(document).ready(function () {
    initEditor();
    initInputs();
});

function initInputs() {
    $selectHouse.select2({
        dropdownParent: $('.house-wrap'),
        placeholder: '',
        allowClear: true,
        ajax: {
            type: "GET",
            url: '../houses/get-houses',
            data: function (params) {
                return {
                    name: params.term,
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

    $selectHouse.on('select2:select', function () {
        const houseId = $(this).val();
        if (houseId > 0) {
            initHouseNestedSelects(houseId, false);
        }
        $checkboxForArrears.prop('checked', false);

    });

    $selectHouse.on('select2:clear', function () {
        this.value = '0';
        $checkboxForArrears.prop('checked', true);
        initHouseNestedSelects(null, true);
    });

    function initHouseNestedSelects(houseId, isDisabled) {
        $selectSection.select2({
            dropdownParent: $('.section-wrap'),
            placeholder: '',
            allowClear: true,
            disabled: isDisabled,
            ajax: {
                type: "GET",
                url: '../sections/get-sections-by-house/' + houseId,
                data: function (params) {
                    return {
                        name: params.term || '',
                        page: (params.page - 1) || 0,
                        pageSize: 10,
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

        $selectFloor.select2({
            dropdownParent: $('.floor-wrap'),
            placeholder: '',
            allowClear: true,
            disabled: isDisabled,
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

        let sectionId;
        let floorId;

        $selectSection.on('select2:select', function () {
            const sectionData = $selectSection.select2('data')[0];
            sectionId = sectionData.id;
            initApartmentSelect(houseId, isDisabled);
        });

        $selectFloor.on('select2:select', function () {
            const floorData = $selectFloor.select2('data')[0];
            floorId = floorData.id;
            initApartmentSelect(houseId, isDisabled)
        });

        initApartmentSelect(houseId, isDisabled);

        function initApartmentSelect(houseId, isDisabled) {
            $selectApartment.select2({
                dropdownParent: $selectApartment.parent(),
                placeholder: '',
                allowClear: true,
                disabled: isDisabled,
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
                        }
                        if (floorId) {
                            parameters.floor = floorId;
                        }
                        return parameters;
                    },
                    processResults: function (response) {
                        return {
                            results: $.map(response.content, function (apartment) {
                                return {
                                    id: apartment.id,
                                    text: (apartment.apartmentNumber).toString().padStart(5, '00000')
                                }
                            }),
                            pagination: {
                                more: !response.last
                            }
                        };
                    }
                }
            });

            $selectSection.add($selectFloor).add($selectApartment).on('select2:clear', function () {
                this.value = '0';
            });
        }
    }
}

function initEditor() {
    const fullToolbar = [
        [{font: []}, {size: []}],
        ['bold', 'italic', 'underline', 'strike'],
        [{color: []}, {background: []}],
        [{script: 'super'}, {script: 'sub'}],
        [{header: '1'}, {header: '2'}, 'blockquote', 'code-block'],
        [{list: 'ordered'}, {list: 'bullet'}, {indent: '-1'}, {indent: '+1'}],
        ['direction', {align: []}],
        ['link'], ['clean']];

    $editorTextMessage = new Quill('#text-message', {
        bounds: '#text-message',
        placeholder: labelTextMessage,
        modules: {
            formula: true,
            toolbar: fullToolbar
        },
        theme: 'snow'
    });
}

$('.button-send').on('click', function () {
    clearAllErrorMessage();
    blockCardDody();

    let formData = new FormData($('#message-form')[0]);
    formData.set('text', $('.ql-editor').html());
    formData.set('textLength', $editorTextMessage.getLength());

    // for (const formDatum of formData.entries()) {
    //     console.log(formDatum)
    // }

    $.ajax({
        url: '',
        type: 'post',
        processData: false,
        contentType: false,
        data: formData,
        success: function (response) {
            toastr.success(successMessageOnSend);
            setTimeout(() =>window.history.back(), 1000);
        },
        error: function (error) {
            printErrorMessageToField(error);
            additionalValidation(error);
            toastr.error(errorMessage);
            console.log(error);
        }
    })
});

function additionalValidation(error) {
    const errorMap = new Map(Object.entries((error.responseJSON)));
    if (errorMap.has('textLength')) {
        let textLengthMessage = errorMap.get('textLength');
        const $text = $('#text-message');
        $text.addClass("is-invalid");
        $text.after($(
            '<p class="error-message invalid-feedback m-0">' + textLengthMessage + '</p>'));
    }
}


