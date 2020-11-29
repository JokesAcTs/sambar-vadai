

function postCovidPositive() {
    var city = $("#city-post-input").val();

    // Fires an Ajax call to the URL defined in the index.js function file
// All URLs to the Advanced I/O function will be of the pattern: /server/{function_name}/{url_path}
    $.ajax({
        url: "/server/CovidTrackerAIO/covid", 
        type: "post",
        contentType: "application/json",
		 headers: {
       'Access-Control-Allow-Origin':'*',
'Access-Control-Allow-Methods': 'HEAD, GET, POST, PUT, PATCH, DELETE',
'Access-Control-Allow-Headers': 'Origin, Content-Type, X-Auth-Token'
       },
        data: JSON.stringify({
            "city_name": city
        }),
        success: function (data) {
            alert(data.message);
			getCovidPositive(city);
        },
        error: function (error) {
            alert(error.message);
        }
    });
}

function getCovidPositive(city) {
    showLoader();
    var positive = "https://media.giphy.com/media/Y1GYiLui9NHcxVKhdo/giphy.gif";
    var negative = "https://media.giphy.com/media/fsPcMdeXPxSP6zKxCA/giphy.gif";


  // Fires an Ajax call to the URL defined in the index.js function file
 // All URLs to the function will be of the pattern: /server/{function_name}/{url_path}
    $.ajax({
        url: "/server/CovidTrackerAIO/covid?city_name=" + city, //Ensure that 'alien_city_function' is the package name of your function
        type: "get",
		headers: {
       'Access-Control-Allow-Origin':'*',
'Access-Control-Allow-Methods': 'HEAD, GET, POST, PUT, PATCH, DELETE',
'Access-Control-Allow-Headers': 'Origin, Content-Type, X-Auth-Token'
       },
        success: function (data) {
            alert(data.message);
            hideLoader();
        },
        errror: function (error) {
            alert(error.message);
        }
    });
}

function showLoader()
{
    $("#result-container").hide();
    $("#loader").show();
}

function hideLoader()
{
    $("#loader").hide();
    $("#result-container").show();
}