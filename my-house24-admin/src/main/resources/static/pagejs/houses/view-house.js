const houseId = window.location.pathname.match(/\d+$/);

$(window).on('load', function () {
    blockCardDody();
    $.ajax({
        url: '../get-view-house/' + houseId,
        type: 'get',
        success: function (response) {
            fillTableWithImages(response);
        },
        error: function (error) {
            toastr.error(errorMessage)
        }
    })
});

function fillTableWithImages(house) {
    $('#edit-link').attr('href', `../edit-house/${houseId}`);
    $('title, #house-title, #house-breadcrumb').html(house.name);
    $('#house-name').html(house.name);
    $('#house-address').html(house.address);
    $('#house-sections').html(house.sectionsCount);
    $('#house-floors').html(house.floorsCount);
    for (const staff of house.staff) {
        $(`<span class="staff-item">${getRoleLabel(staff.role.name)} : 
            <a href="../../system-settings/staff/view-staff/${staff.id}">${staff.firstName} ${staff.lastName}</a>
            </span>`).appendTo('#house-staff');
    }

    $('#image1').attr('src', house.image1 ? uploadsPath + house.image1 : placeholderImage);
    $('#image2').attr('src', house.image2 ? uploadsPath + house.image2 : placeholderImage);
    $('#image3').attr('src', house.image3 ? uploadsPath + house.image3 : placeholderImage);
    $('#image4').attr('src', house.image4 ? uploadsPath + house.image4 : placeholderImage);
    $('#image5').attr('src', house.image5 ? uploadsPath + house.image5 : placeholderImage);
}

function getRoleLabel(role) {
    switch (role) {
        case 'DIRECTOR':
            return roleDirector;
        case 'MANAGER':
            return roleManager;
        case 'ACCOUNTANT':
            return roleAccountant;
        case 'ELECTRICIAN':
            return roleElectrician;
        case 'PLUMBER':
            return rolePlumber;
    }
}