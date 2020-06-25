
var DialogGlobal= {
    dialogStyle:function (dialog,size) {
        dialog.getModalDialog().css('width', size.width);
        dialog.getModalHeader().css('width', size.width);
        dialog.getModalHeader().css('background-color', '#192b63');
        dialog.getModalBody().css('width', size.width);
        dialog.getModalBody().css('height', size.height);
        dialog.getModalBody().css('color', '#697dca');
        dialog.getModalFooter().css('width', size.width);
        dialog.getModalFooter().css('background-color', '#192b63');
        dialog.getModalFooter().css('height', '55px');
        dialog.getModalFooter().css('padding-top', '10px');
    },
    registerDialog:function (dialog) {
        if (!$.fn.DialogGlobal_RegisterMap)
            $.fn.DialogGlobal_RegisterMap = new Map();
        $.fn.DialogGlobal_RegisterMap.set(dialog.dialogId,dialog);
        console.info("dialog["+dialog.dialogId+"] is Register");
    },
    unRegisterDialog:function (dialogId) {
        if ($.fn.DialogGlobal_RegisterMap)
            $.fn.DialogGlobal_RegisterMap.delete(dialogId);
        console.info("dialog["+dialogId+"] is unRegister");
    },
    getRegisterDialog:function(dialogId){
        if ($.fn.DialogGlobal_RegisterMap)
            return $.fn.DialogGlobal_RegisterMap.get(dialogId);
        return null;
    },
    nomalSize:function () {
        return {width:"800px",height:"370px"};
    }
}