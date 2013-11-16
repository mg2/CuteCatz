function catreload () {
			var cat = document.getElementById('catimg');
			cat.src = 'http://thecatapi.com/api/images/get?format=src&cachebuster='+Math.random();
			alert(cat.src);
		}