$(document).ready(function() {
    $('#searchAddress').on('click', function() {
        const query = $('#restaurantName').val();
        if (!query) {
            alert('Please enter a restaurant name.');
            return;
        }

        $.ajax({
            url: '/api/restaurant/search',
            method: 'GET',
            data: { query: query },
            success: function(response) {
                if (response.items && response.items.length > 0) {
                    const address = response.items[0].roadAddress || response.items[0].address;
                    $('#address').val(address);
                } else {
                    alert('No results found.');
                }
            },
            error: function() {
                alert('Error occurred while searching for address.');
            }
        });
    });
});
