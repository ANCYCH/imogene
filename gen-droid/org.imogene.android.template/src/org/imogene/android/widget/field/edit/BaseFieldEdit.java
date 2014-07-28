package org.imogene.android.widget.field.edit;

import java.util.ArrayList;

import org.imogene.android.template.R;
import org.imogene.android.widget.ErrorAdapter.ErrorEntry;
import org.imogene.android.widget.field.BaseField;
import org.imogene.android.widget.field.ConstraintBuilder;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import fr.medes.android.database.sqlite.stmt.Where;

public abstract class BaseFieldEdit<T> extends BaseField<T> implements ConstraintBuilder {

	public interface OnValueChangeListener {
		public void onValueChange(BaseFieldEdit<?> field);
	}

	private View mRequiredView;
	private View mHelpView;

	private ArrayList<BaseField<?>> mConstraintDependents;

	private OnValueChangeListener mListener;

	private boolean mNotifyValueChanged = true;
	private boolean mAutomaticVisibility = true;
	private boolean mReadOnly;
	private boolean mRequired;
	private int mHelpId;

	private Dialog mHelpDialog;

	public BaseFieldEdit(Context context, int layoutId) {
		super(context, layoutId);
		init();
	}

	public BaseFieldEdit(Context context, AttributeSet attrs, int layoutId) {
		super(context, attrs, layoutId);
		init();
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BaseFieldEdit, 0, 0);
		setHelpId(a.getResourceId(R.styleable.BaseFieldEdit_help, 0));
		setReadOnly(a.getBoolean(R.styleable.BaseFieldEdit_readOnly, false));
		setRequired(a.getBoolean(R.styleable.BaseFieldEdit_required, false));
		a.recycle();
	}

	private void init() {
		mHelpView = findViewById(R.id.imog__help);
		if (mHelpView != null) {
			mHelpView.setSaveEnabled(false);
		}

		mRequiredView = findViewById(R.id.imog__required);
		if (mRequiredView != null) {
			mRequiredView.setSaveEnabled(false);
		}
	}

	@Override
	public void init(T value) {
		disableNotifyValueChanged();
		setValue(value);
		enableNotifyValueChanged();
	}

	public void setRequired(boolean required) {
		mRequired = required;
		if (mRequiredView != null) {
			mRequiredView.setVisibility(required ? View.VISIBLE : View.GONE);
		}
	}

	public boolean isRequired() {
		return mRequired;
	}

	/**
	 * Set if the field is read-only or not.
	 * 
	 * @param readOnly {@code true} if the is read-only, {@code false} otherwise.
	 */
	public void setReadOnly(boolean readOnly) {
		mReadOnly = readOnly;
		setEnabled(!readOnly);
	}

	/**
	 * Returns if the field is read-only or not.
	 * 
	 * @return Whether the field is read-only or not.
	 */
	public boolean isReadOnly() {
		return mReadOnly;
	}

	/**
	 * Checks if the field is valid or not given the constraints (required, validation rules).
	 * 
	 * @return Whether the field is valid or not.
	 */
	public boolean isValid() {
		return mRequired ? !isEmpty() : true;
	}

	/**
	 * Set the help display resource to display when help is clicked.
	 * 
	 * @param helpId The help display resource identifier.
	 */
	public void setHelpId(int helpId) {
		mHelpId = helpId;
		if (mHelpView != null) {
			mHelpView.setOnClickListener(helpId > 0 ? this : null);
			mHelpView.setVisibility(helpId > 0 ? View.VISIBLE : View.GONE);
		}
	}

	/**
	 * Returns the help display resource identifier.
	 * 
	 * @return The help display resource identifier.
	 */
	public int getHelpId() {
		return mHelpId;
	}

	public ErrorEntry getErrorEntry(int tag) {
		ErrorEntry entry = new ErrorEntry();
		entry.setField(this);
		entry.setTag(tag);
		entry.setTitle(getTitle());
		if (mRequired) {
			entry.addMessage(getResources().getString(R.string.imog__required));
		}
		return entry;
	}

	public void setAutomaticManageVisibility(boolean automatic) {
		mAutomaticVisibility = automatic;
	}

	@Override
	public void onDependencyChanged() {
		if (!mAutomaticVisibility) {
			return;
		}
		super.onDependencyChanged();
	}

	protected void enableNotifyValueChanged() {
		if (!mNotifyValueChanged) {
			mNotifyValueChanged = true;
		}
	}

	protected void disableNotifyValueChanged() {
		if (mNotifyValueChanged) {
			mNotifyValueChanged = false;
		}
	}

	@Override
	protected void onValueChange() {
		super.onValueChange();
		if (mRequired && mRequiredView != null) {
			mRequiredView.setVisibility(isEmpty() ? View.VISIBLE : View.GONE);
		}
		if (mNotifyValueChanged) {
			notifyValueChange();
			notifyConstraintDependentsChange();
		}
	}

	public void setOnValueChangeListener(OnValueChangeListener listener) {
		mListener = listener;
	}

	private void notifyValueChange() {
		if (mListener != null) {
			mListener.onValueChange(this);
		}
	}

	@Override
	public void registerConstraintDependent(BaseField<?> dependent) {
		if (mConstraintDependents == null) {
			mConstraintDependents = new ArrayList<BaseField<?>>();
		}

		mConstraintDependents.add(dependent);
	}

	@Override
	public Where onCreateConstraint(String column) {
		return null;
	}

	private void notifyConstraintDependentsChange() {
		if (mConstraintDependents == null) {
			return;
		}

		final int size = mConstraintDependents.size();
		for (int i = 0; i < size; i++) {
			mConstraintDependents.get(i).setValue(null);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imog__help) {
			showHelpDialog(null);
		} else {
			dispatchClick(v);
		}
	}

	private void showHelpDialog(Bundle state) {
		Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(getTitle());
		builder.setMessage(mHelpId);
		builder.setPositiveButton(android.R.string.ok, null);

		getFieldManager().registerOnActivityDestroyListener(this);

		final Dialog dialog = mHelpDialog = builder.create();
		if (state != null) {
			dialog.onRestoreInstanceState(state);
		}
		dialog.setOnDismissListener(this);
		dialog.show();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if (dialog.equals(mHelpDialog)) {
			mHelpDialog = null;
		}
	}

	@Override
	public void onActivityDestroy() {
		super.onActivityDestroy();
		if (mHelpDialog != null && mHelpDialog.isShowing()) {
			mHelpDialog.dismiss();
		}
	}

}
