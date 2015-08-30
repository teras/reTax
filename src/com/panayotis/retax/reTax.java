package com.panayotis.retax;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;
import java.util.Random;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import javax.microedition.rms.RecordEnumeration;

public final class reTax extends MIDlet implements CommandListener, ItemStateListener {

    /** Soft button for exiting the game. */
    private final Command exitCmd  = new Command("Exit", Command.EXIT, 1);

    private final Command editCmd = new Command("Rates", null, Command.SCREEN, 1);
    private final Command returnCmd = new Command("Return", Command.BACK, 1);
    private final Command aboutCmd = new Command("About", Command.SCREEN, 1);

    /**
     * Creates the reTax Application
     */

	private final Form mainform = new Form("reTax");
	private final Form editform = new Form("Edit Tax Rates");

	private final TextField input = new TextField("Given value","",8,TextField.DECIMAL);
	private final StringItem result = new StringItem("New value","");
	private final StringItem netv = new StringItem("Net value","");

	private final TextField origtax = new TextField("Old Tax", "", 5, TextField.DECIMAL);
	private final TextField newtax = new TextField("New Tax", "", 5, TextField.DECIMAL);
	
	private float tax1 = 18f;
	private float tax2 = 13f;
	
	private RecordStore store = null;
	private static final String storename = "reTax_Store";
	
	public reTax() {
        mainform.addCommand(exitCmd);
        mainform.addCommand(editCmd);
        mainform.setCommandListener(this);
		mainform.setItemStateListener(this);

		mainform.append(input);
		mainform.append(result);
		mainform.append(netv);

		input.setLayout(Item.LAYOUT_EXPAND | Item.LAYOUT_NEWLINE_AFTER);
		result.setLayout(Item.LAYOUT_EXPAND| Item.LAYOUT_NEWLINE_AFTER);
		netv.setLayout(Item.LAYOUT_EXPAND  | Item.LAYOUT_NEWLINE_AFTER);
		
		editform.addCommand(returnCmd);
		editform.addCommand(aboutCmd);
        editform.setCommandListener(this);

		editform.append(origtax);
		editform.append(newtax);

		origtax.setLayout(Item.LAYOUT_EXPAND | Item.LAYOUT_NEWLINE_AFTER);
		newtax.setLayout(Item.LAYOUT_EXPAND| Item.LAYOUT_NEWLINE_AFTER);
		
		try {
			store = RecordStore.openRecordStore( storename, true);
		}
		catch (RecordStoreException e) {}
	}
		
	
    protected void startApp() {
		readData();
		recalcValue();
        Display.getDisplay(this).setCurrent(mainform);
    }

    protected void destroyApp(boolean unconditional) {
		if ( store != null ) {
			try {
				store.closeRecordStore();
			}
			catch (RecordStoreException e) {
				e.printStackTrace();
			}
		}
	}

    protected void pauseApp() {}

    /**
     * Responds to commands issued on CalculatorForm.
     *
     * @param c command object source of action
     * @param d screen object containing actioned item
     */
    public void commandAction(Command c, Displayable d) {
        if (c == exitCmd) {
            destroyApp(false);
            notifyDestroyed();
            return;
        }
		
		if ( c == editCmd ) {
			Display.getDisplay(this).setCurrent(editform);
			origtax.setString(String.valueOf(tax1));
			newtax.setString(String.valueOf(tax2));
			return;
		}

		if ( c == returnCmd ) {
			try {
				tax1 = Float.parseFloat(origtax.getString());
				tax2 = Float.parseFloat(newtax.getString());
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
			}

			Display.getDisplay(this).setCurrent(mainform);
			recalcValue();
			saveData();
			return;
		}
	
		if ( c == aboutCmd ) {
			String infotext = "The aim of this program is to help you recalculate the amount of money, with different tax rates. \nIn the input box insert the amount of money and the program will recalculate this amount with the new tax percentage. The net amount (without tax) will be displayed also.\n\nIn the Rates dialog enter the two tax rates (in percentage). E.g. for an 17.5% tax rate, input 17.5\n\nThis program is under the GNU Licence. Copyright 2004 by Panayotis Katsaloulis (panayotis@panayotis.com)";
			Alert alert = new Alert("About reTax 1.0", infotext, null, AlertType.INFO);
			alert.setTimeout(Alert.FOREVER);
			Display.getDisplay(this).setCurrent(alert);
		}
    }

	
	
	public void itemStateChanged(Item it) {
		if ( it == input) {
			recalcValue();
		}
	}
	
	private void recalcValue() {
		float num = 0;
		try {
			num = Float.parseFloat(input.getString());
		}
		catch (NumberFormatException e) { /* Ignore this error */ }
		num *= (100f+tax2)/(100f+tax1);
		result.setText(String.valueOf(num));
		netv.setText(String.valueOf(num*100/(100f+tax2)));
	}

	private void saveData() {
		byte[] data;
		try {
			int rec;
			/* Clean records */
			RecordEnumeration recs = store.enumerateRecords(null, null, false);
			while ( recs.hasNextElement()) {
				rec = recs.nextRecordId();
				store.deleteRecord(rec);
			}

			/* Store the tax1 variable */
			data = ('o'+String.valueOf(tax1)).getBytes();
			store.addRecord(data, 0, data.length);

			/* Store the tax2 variable */
			data = ('n'+String.valueOf(tax2)).getBytes();
			store.addRecord(data, 0, data.length);
		}
		catch (RecordStoreException e) {
			e.printStackTrace();
		}
	}

	private void readData() {
	
		
		byte[] data;
		String num;

		try {	
			RecordEnumeration recs = store.enumerateRecords(null, null, false);
			while ( recs.hasNextElement() ) {
				num = new String(recs.nextRecord());
				if (num.charAt(0)=='o') {
					tax1 = Float.parseFloat(num.substring(1));
				}
				if (num.charAt(0)=='n') {
					tax2 = Float.parseFloat(num.substring(1));
				}
			}
		}
		catch (RecordStoreException e) {
			e.printStackTrace();
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
}
