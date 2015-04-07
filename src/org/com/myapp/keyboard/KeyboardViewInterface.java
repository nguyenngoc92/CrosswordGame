
package org.com.myapp.keyboard;

public interface KeyboardViewInterface {
	public void onKeyDown(String value, int location[], int width);
	public void onKeyUp(String string);
	public void setDraft(boolean isDraft);
}
