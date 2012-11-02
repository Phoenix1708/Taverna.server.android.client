package cs.man.ac.uk.taverna.server.android.client.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MessageDialog extends AlertDialog {
	
	// Dialog Message String
	private String dialogMessage = "Unknown dialog message. Please contact developer for support.";
	private Context dialogContext;
	// Constructor which takes the input message string
	// that used to display
	public MessageDialog(Context context, String message) {
		super(context);	
		if (message != null){
			this.dialogMessage = message;
		}		
		this.dialogContext = context;
	}
	
	// Method to create the dialog
    public AlertDialog createDialog() {   	
    	
        Builder builder = new Builder(this.dialogContext);
        builder.setMessage(dialogMessage)
               .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   // dismiss the dialog
                       dialog.dismiss();
                   }
               });

        return builder.create();
    }
}
