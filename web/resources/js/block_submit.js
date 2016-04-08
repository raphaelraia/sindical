function arrumaEnter(field, event) {
    alert('aaa');
    var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
    if (keyCode == 13) {
        var i;
        for (i = 0; i < field.form.elements.length; i++)
            if (field == field.form.elements[i])
                break;
        i = (i + 1) % field.form.elements.length;
        field.form.elements[i].focus();
        return false;
    }
    return true;
}


function bloquearCtrlJ() {   // Verificação das Teclas
    var tecla = window.event.keyCode;   //Para controle da tecla pressionada
    var ctrl = window.event.ctrlKey;    //Para controle da Tecla CTRL
    if (ctrl && tecla == 74) {    //Evita teclar ctrl + j
        event.keyCode = 0;
        event.returnValue = false;
    }
}