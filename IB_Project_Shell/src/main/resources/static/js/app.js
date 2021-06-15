// definisanje globaljn ih promenljivih
var username;
var roles;

// funkcija za logovanje
function login() {
	
	// kreiramo JavaScript objekat sa podacima koje je korisnik uneo u input polja
	var user = {
		'username' : $('#username').val(),
		'password': $('#password').val()
	}
	
	// podatke na backend uvek saljemo kao JSON string
	var userJSON = JSON.stringify(user);
	
	$.ajax({
	    url : '/auth/login',
	    type: 'POST',
	    data : userJSON,
	    contentType:"application/json; charset=utf-8",
	    dataType:"json",
	    success: function(data)
	    {
	    	// 1. sakrij poruku o gresci ako su pogresni kredencijali posto je korisnik uspesno ulogovan!
	    	$('#wrongCredentialsError').hide();
	    	
	    	// 2. korak: sacuvamo JWT token koji server salje u LocalStorage pod kljucem "jwt"
	    	// sacuvan token mozete videti na sledeci nacin: Otvorite Developer konzolu (F12) -> Application -> Local Storage
	    	var jwt = data.accessToken;
	    	localStorage.setItem('jwt', jwt);
	    	
	    	// 3. iz tokena mozemo da citamo podatke koji su poslati sa serverske strane i cuvati ih u pormenljivima, LocalStorage-u, ...
	    	// mi hocemo da procitamo podatke iz jwt tokena koji smo dobili i upisemo username u promenljivu "username" i uloge u "roles" promenljivu
	    	var decodedJWTData = _decodeJWT(jwt);
	    	if (decodedJWTData != null) {
	    		username = decodedJWTData.sub;
		    	roles = decodedJWTData.roles;
	    	}
	    	
	    	// 4. prikazemo novu formu
	    	showAnotherForm();
	    	
	    },
	    error: function (error)
	    {
	    	// prikazi poruku o gresci ako su pogresni kredencijali
	    	$('#wrongCredentialsError').show();
	    }
	});
}

function logout() {
	// posto se koristi autentifikacija putem token, sto predstavlja stateless komunikaciju, server ne pamti stanje o ulogovanim korisnicima
	// sto znaci da se ne salje nikakav zahtev na bekend.
	// Potrebno je samo obrisati JWT token iz LocalStorage-a.
	// Kada se posalje zahtev bez tokena u header-u, server smatra da je zahtev neautorizovan!
	// Probajte kada pozovete ovu metodu da ponovo posaljete zahtev na neku od ruta
	
	localStorage.removeItem('jwt');
	
	$('#welcomeMessage').text('You are not currently logged in!');
	
	$('#getAllUsersError').hide();
	$('#getAllUsersSuccess').hide();
	$('#whoAmIError').hide();
	$('#whoAmISuccess').hide();
	$('#fooError').hide();
	$('#fooSuccess').hide();
	
}

function goToLogin() {
	// vrednosti u LocalStorage-u "prezive" reload stranice i zatvaranje Browser-a, pa ukoliko ima potrebe morate rucno obrisati token 
	// iz LocalStorage ili obrisati kes u browser-u
	
	localStorage.clear();
	location.reload();
}

function showAnotherForm() {
	$('#welcomeMessage').text('Hello ' + username + '! Your roles: ' + roles);
	
	// sakrijemo Login formu
	$('#loginForm').hide();
	
	// prikazemo drugu formu
	$('#otherForm').show();
}

function whoAmI() {
	$.ajax({
	    url : '/api/whoami',
	    type: 'GET',
	    contentType:"application/json; charset=utf-8",
	    dataType:"json",
	    headers: {'Authorization': 'Bearer ' + localStorage.getItem('jwt')}, // saljemo token u Authorization header-u gde ga serverska strana ocekuje
	    success: function(data)
	    {
	    	console.log('Who Am I - Response:')
	    	console.log(data);
	    	console.log("===========================================================================");
	    	
	    	
	    	$('#whoAmIError').hide();
	    	$('#whoAmISuccess').show();
	    	
	    },
	    error: function (error)
	    {
	    	$('#whoAmIError').show();
	    	$('#whoAmISuccess').hide();
	    }
	});
}

function getAllUsers() {
	$.ajax({
	    url : '/api/user/all',
	    type: 'GET',
	    contentType:"application/json; charset=utf-8",
	    dataType:"json",
	    headers: {'Authorization': 'Bearer ' + localStorage.getItem('jwt')}, // saljemo token u Authorization header-u gde ga serverska strana ocekuje
	    success: function(data)
	    {
	    	console.log('Get All Users - Response:')
	    	console.log(data);
	    	console.log("===========================================================================");
	    	
	    	$('#getAllUsersError').hide();
	    	$('#getAllUsersSuccess').show();
	    	
	    },
	    error: function (error)
	    {
	    	$('#getAllUsersError').show();
	    	$('#getAllUsersSuccess').hide();
	    }
	});
}

function foo() {
	$.ajax({
	    url : '/api/foo',
	    type: 'GET',
	    contentType:"application/json; charset=utf-8",
	    dataType:"json",
	    headers: {'Authorization': 'Bearer ' + localStorage.getItem('jwt')}, // saljemo token u Authorization header-u gde ga serverska strana ocekuje
	    success: function(data)
	    {
	    	console.log('Foo - Response:')
	    	console.log(data);
	    	console.log("===========================================================================");
	    	
	    	$('#fooError').hide();
	    	$('#fooSuccess').show();
	    	
	    },
	    error: function (error)
	    {
	    	$('#fooError').show();
	    	$('#fooSuccess').hide();
	    }
	});
}


// funkcija za citanje podataka iz jwt tokena (payload)
function _decodeJWT(token) {
	try {
		var decodedData = JSON.parse(atob(token.split('.')[1]));
		
		console.log('Decoded JWT token:');
		console.log(decodedData);
		console.log("===========================================================================");
		
	    return decodedData;
	  } catch (e) {
		console.log('Error decoding JWT. JWT Token is null.');
	    return null;
	  }
	
	
}