// definisanje globaljn ih promenljivih
var username;
var roles;

// funkcija za logovanje
function login() {
	
	// kreiramo JavaScript objekat sa podacima koje je korisnik uneo u input polja
	var user = {
		'username' : $('#email').val(),
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
			$('#jksId').append('<button type="button" onclick="getJks(\''+ username +'\')" class="btn btn-primary">Get jks file</button>');
			/*var pos=roles.indexOf(" ");
			if(pos>0){
				var subs2=roles.substring(pos+1, roles.length-1);
			}
			var subs1=roles.substring(0, pos);
			console.log(subs2);
			console.log(subs1);
			if(subs1!="ROLE_ADMIN" && subs2!="ROLE_ADMIN"){
				$('#allUsersForm').remove();
			}*/
	    	
	    },
	    error: function (error)
	    {
	    	// prikazi poruku o gresci ako su pogresni kredencijali
	    	$('#wrongCredentialsError').show();
	    }
	});
} 

function register() {
	
	// kreiramo JavaScript objekat sa podacima koje je korisnik uneo u input polja
	var user = {
		'email' : $('#emailRegister').val(),
		'password': $('#passwordRegister').val()
	}
	
	// podatke na backend uvek saljemo kao JSON string
	var userJSON = JSON.stringify(user);
	
	$.ajax({
	    url : '/auth/signup',
	    type: 'POST',
	    data : userJSON,
	    contentType:"application/json; charset=utf-8",
	    dataType:"json",
		success: function(data)
	    {  	
			console.log("U success sam");
			console.log(data);
	    	alert(data.r);
	    	// 4. prikazemo login formu
	    	showLoginForm();
		},
		error: function (data)
	    {
			console.log("U erroru sam");
			console.log(data);
	    	alert(data.responseJSON.r);
	    }
	});
}

function signup() {
	
	// prikazujemo Register formu
	$('#registerForm').show();
	
	// sakrijemo Login formu
	$('#loginForm').hide();
	
	// sakrijemo drugu formu
	$('#otherForm').hide();
	
}

function logout() {
	// posto se koristi autentifikacija putem token, sto predstavlja stateless komunikaciju, server ne pamti stanje o ulogovanim korisnicima
	// sto znaci da se ne salje nikakav zahtev na bekend.
	// Potrebno je samo obrisati JWT token iz LocalStorage-a.
	// Kada se posalje zahtev bez tokena u header-u, server smatra da je zahtev neautorizovan!
	// Probajte kada pozovete ovu metodu da ponovo posaljete zahtev na neku od ruta
	
	localStorage.removeItem('jwt');
	
	$('#otherForm').hide();
	goToLogin();
	
}

function goToLogin() {
	// vrednosti u LocalStorage-u "prezive" reload stranice i zatvaranje Browser-a, pa ukoliko ima potrebe morate rucno obrisati token 
	// iz LocalStorage ili obrisati kes u browser-u
	
	localStorage.clear();
	location.reload();
}

function showLoginForm() {
	
	// prikazujemo Login formu
	$('#loginForm').show();
	$('#registerForm').hide();
}

function showAnotherForm() {
	$('#welcomeMessage').text('Hello ' + username + '!');
	
	// sakrijemo Login formu
	$('#loginForm').hide();
	
	// prikazemo drugu formu
	$('#otherForm').show();
}

function sendMessage() {

	var emailAddress=$('#emailReciever');
	var subject=$('#subjectMessage');
	var content=$('#contentMessage');

	var message = {
		'emailAddress' : emailAddress.val(),
		'sender' : username,
		'subject' : subject.val(),
		'content': content.val()
	}

	// podatke na backend uvek saljemo kao JSON string
	var messageJSON = JSON.stringify(message);

	$.ajax({
	    url : '/api/message/send',
		type: 'POST',
		data : messageJSON,
	    contentType:"application/json; charset=utf-8",
	    dataType:"json",
	    headers: {'Authorization': 'Bearer ' + localStorage.getItem('jwt')}, // saljemo token u Authorization header-u gde ga serverska strana ocekuje
	    success: function(data)
	    {
	    	console.log("U success sam");
			console.log(data);
			alert(data.r);
			emailAddress.val("");
			subject.val("");
			content.val("");
	    },
	    error: function (data)
	    {
	    	console.log("U erroru sam");
			console.log(data);
			alert(data.responseJSON.r);
	    }
	});

}

function showCreateMessage() {
	var createMessage=$('#createMessage');
	createMessage.show();
}

function changeActive(email) {
	
	console.log("email: "+email);
	var checkboxId=$('[name=\''+email+'\']'); 
	
	console.log("checkboxId: "+checkboxId.is(':checked'));
	var checked=false;
	if(checkboxId.is(':checked')){
		console.log("CEKIRAN "+checkboxId.is(':checked'));
		checked=true;
	}else{
		console.log("ODCEKIRAN "+checkboxId.is(':checked'));
	}
	
	$.ajax({
	    url : '/auth/activate',
	    type: 'POST',
	    data : JSON.stringify({'email':email, 'active':checked}),
	    contentType:"application/json; charset=utf-8",
	    dataType:"json",
	    headers: {'Authorization': 'Bearer ' + localStorage.getItem('jwt')}, // saljemo token u Authorization header-u gde ga serverska strana ocekuje
	    success: function(data)
	    {
	    	console.log("Uspesno de/aktiviran");
	    	getAllUsers();
	    }
	});
}

function getAllUsers() {
	
	var emailFilterInput=$('#emailFilterInput');
	var emailFilter=emailFilterInput.val();
	var usersTable = $('#usersTable');

	if(emailFilter === ""){
		emailFilter="empty";
	}

	console.log('emailFilter: ' + emailFilter);
	
	$.ajax({
	    url : '/api/users/'+emailFilter,
	    type: 'GET',
	    contentType:"application/json; charset=utf-8",
	    dataType:"json",
	    headers: {'Authorization': 'Bearer ' + localStorage.getItem('jwt')}, // saljemo token u Authorization header-u gde ga serverska strana ocekuje
	    success: function(data)
	    {
	    	usersTable.find('tr:gt(1)').remove();
	    	usersTable.show();
	    	var users = data;
			for (it in users) {
				var authorities=users[it].authorities;
				var authoritiesString="";
				for(jt in authorities){
					if(jt==(authorities.length-1)){
						authoritiesString=authoritiesString+authorities[jt].authority;
					}else{
						authoritiesString=authoritiesString+authorities[jt].authority+", ";
					}
				}
				var chacked="";
				if(users[it].enabled){
					checked='<input type="checkbox" onclick="changeActive(\''+ users[it].username +'\')" name="'+ users[it].username +'" class="select-row" checked>';
				}else{
					checked='<input type="checkbox" onclick="changeActive(\''+ users[it].username +'\')" name="'+ users[it].username +'" class="select-row">';
				}
				usersTable.append(
					'<tr>' +  
						'<td>' + users[it].username + '</td>' + 
						'<td>' + authoritiesString + '</td>' +
						'<td>' + checked + '</td>' +
						//'<td> <a href="C:\Users\Dejan\git\IBProjekat\IBProjekat\data\rakindejan@gmail.com.jks""_blank">Download</a> </td>'+
						'<td>' + '<button type="button" onclick="getCertificate(\''+ users[it].username +'\')" class="btn btn-primary">Download certificate</button>' + '</td>' +
					'</tr>'
							)	
				console.log('checked: '+checked);
			}
	    	
	    	console.log('Get All Users - Response:');
	    	console.log(data);
	    	console.log("===========================================================================");
	    	
	    	/*$('#getAllUsersError').hide();
	    	$('#getAllUsersSuccess').show();*/
	    	
	    },
	    error: function (error)
	    {
	    	$('#getAllUsersError').show();
	    	$('#getAllUsersSuccess').hide();
	    }
	});
}

function getCertificate(email){

	console.log("email: "+email);
	
	$.ajax({
	    url : '/api/certificate/'+email,
	    type: 'GET',
	    contentType:"application/json; charset=utf-8",
	    dataType:"json",
	    headers: {'Authorization': 'Bearer ' + localStorage.getItem('jwt')}, // saljemo token u Authorization header-u gde ga serverska strana ocekuje
	    success: function(data)
	    {
			var file = new File([data], email+".cer");
			saveAs(file);
	    }
	});	

}

function getJks(email){

	console.log("email: "+email);
	
	$.ajax({
	    url : '/api/jks/'+email,
	    type: 'GET',
	    contentType:"application/json; charset=utf-8",
	    dataType:"json",
	    headers: {'Authorization': 'Bearer ' + localStorage.getItem('jwt')}, // saljemo token u Authorization header-u gde ga serverska strana ocekuje
	    success: function(data)
	    {
			console.log("Usepsno su stigli podaci!");
			console.log(data);
			if(data === true){
				alert("You have successfully downloaded the file!");
			}
		},
		error: function (error)
	    {
	    	console.log(error);
	    }
	});	

}

function download(filename, text) {
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);

    element.style.display = 'none';
    document.body.appendChild(element);

    element.click();

    document.body.removeChild(element);
}

function getMessages() {
	
	var meessagesTable = $('#messagesTable');
	
	$.ajax({
	    url : '/api/message/all/'+username,
	    type: 'GET',
	    contentType:"application/json; charset=utf-8",
	    dataType:"json",
	    headers: {'Authorization': 'Bearer ' + localStorage.getItem('jwt')}, // saljemo token u Authorization header-u gde ga serverska strana ocekuje
	    success: function(data)
	    {
	    	meessagesTable.find('tr:gt(1)').remove();
			meessagesTable.show();
			console.log(data.messages.length)
			var messages = data.messages;
			var signature = data.signature;
			if(messages.length===0){
				$('#messagesForm').append('<h2>Nemate jos ni jednu poruku!</h2>');
				meessagesTable.remove();
			}else{
				for (it in messages) {
					meessagesTable.append(
						'<tr>' +  
							'<td>' + '<input readonly="false" type="email" class="form-control" value="'+ messages[it].sender +'">' + '</td>' + 
							'<td>' + '<input readonly="false" type="text" class="form-control" value="'+ messages[it].subject +'">' + '</td>' +
							'<td>' + '<textarea readonly="false" class="form-control">'+ messages[it].content +'</textarea>' + '</td>' +
						'</tr>'
								)	
				}
			}
			
			if(signature === false && messages.length>0){
				alert("-----Signature is invalid!-----");
			}

	    	console.log('Get Messages - Response:');
	    	console.log(data);
	    	console.log("===========================================================================");
	    	
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