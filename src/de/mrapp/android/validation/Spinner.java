/*
 * AndroidMaterialValidation Copyright 2015 Michael Rapp
 *
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>. 
 */
package de.mrapp.android.validation;

import de.mrapp.android.validation.adapter.ProxySpinnerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

/**
 * A view, which allows to choose a value from a drop down menu. The value may
 * be validated according to the pattern, which is suggested by the Material
 * Design guidelines.
 * 
 * @author Michael Rapp
 *
 * @since 1.0.0
 */
public class Spinner extends
		AbstractValidateableView<android.widget.Spinner, Object> {

	/**
	 * The hint, which is displayed, when no item is selected.
	 */
	private CharSequence hint;

	/**
	 * The color of the hint, which is displayed, when no item is selected.
	 */
	private ColorStateList hintColor;

	/**
	 * The listener, which should be notified, when an item has been selected.
	 */
	private OnItemSelectedListener itemSelectedListener;

	/**
	 * Initializes the view.
	 * 
	 * @param attributeSet
	 *            The attribute set, the attributes should be obtained from, as
	 *            an instance of the type {@link AttributeSet}
	 */
	private void initialize(final AttributeSet attributeSet) {
		obtainStyledAttributes(attributeSet);
		setLineColor(getAccentColor());
		getView().setOnItemSelectedListener(createItemSelectedListener());
	}

	/**
	 * Obtains all attributes from a specific attribute set.
	 * 
	 * @param attributeSet
	 *            The attribute set, the attributes should be obtained from, as
	 *            an instance of the type {@link AttributeSet}
	 */
	private void obtainStyledAttributes(final AttributeSet attributeSet) {
		TypedArray typedArray = getContext().obtainStyledAttributes(
				attributeSet, R.styleable.Spinner);
		try {
			obtainHint(typedArray);
			obtainHintColor(typedArray);
			obtainSpinnerStyledAttributes(typedArray);
		} finally {
			typedArray.recycle();
		}
	}

	/**
	 * Obtains the hint, which should be shown, when no item is selected, from a
	 * specific typed array.
	 * 
	 * @param typedArray
	 *            The typed array, the hint should be obtained from, as an
	 *            instance of the class {@link TypedArray}
	 */
	private void obtainHint(final TypedArray typedArray) {
		setHint(typedArray.getText(R.styleable.Spinner_android_hint));
	}

	/**
	 * Obtains the color of the hint, which should be shown, when no item is
	 * selected, from a specific typed array.
	 * 
	 * @param typedArray
	 *            The typed array, the hint should be obtained from, as an
	 *            instance of the class {@link TypedArray}
	 */
	private void obtainHintColor(final TypedArray typedArray) {
		ColorStateList colors = typedArray
				.getColorStateList(R.styleable.Spinner_android_textColorHint);

		if (colors == null) {
			TypedArray styledAttributes = getContext().getTheme()
					.obtainStyledAttributes(
							new int[] { android.R.attr.textColorSecondary });
			colors = ColorStateList.valueOf(styledAttributes.getColor(0, 0));
		}

		setHintTextColor(colors);
	}

	/**
	 * Obtains all attributes, which are defined by an
	 * {@link android.widget.Spinner} widget, from a specific typed array.
	 * 
	 * @param typedArray
	 *            The typed array, the attributes should be obtained from, as an
	 *            instance of the class {@link TypedArray}
	 */
	private void obtainSpinnerStyledAttributes(final TypedArray typedArray) {
		for (int i = 0; i < typedArray.getIndexCount(); i++) {
			int index = typedArray.getIndex(i);

			if (index == R.styleable.Spinner_android_dropDownHorizontalOffset) {
				setDropDownHorizontalOffset(typedArray.getDimensionPixelSize(
						index, getDropDownHorizontalOffset()));
			} else if (index == R.styleable.Spinner_android_dropDownVerticalOffset) {
				setDropDownVerticalOffset(typedArray.getDimensionPixelSize(
						index, getDropDownVerticalOffset()));
			} else if (index == R.styleable.Spinner_android_dropDownWidth) {
				setDropDownWidth(typedArray.getLayoutDimension(index,
						getDropDownWidth()));
			} else if (index == R.styleable.Spinner_android_popupBackground) {
				Drawable popupBackground = typedArray.getDrawable(index);

				if (popupBackground != null) {
					setPopupBackgroundDrawable(popupBackground);
				}
			} else if (index == R.styleable.Spinner_android_prompt) {
				String prompt = typedArray.getString(index);

				if (prompt != null) {
					setPrompt(prompt);
				}
			} else if (index == R.styleable.Spinner_android_entries) {
				CharSequence[] entries = typedArray.getTextArray(index);

				if (entries != null) {
					ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
							getContext(), R.layout.spinner_item, entries);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					setAdapter(adapter);
				}
			}
		}
	}

	/**
	 * Returns the color of the theme attribute
	 * <code>android.R.attr.colorControlNormal</code>.
	 * 
	 * @return The color of the theme attribute
	 *         <code>android.R.attr.colorControlNormal</code>
	 */
	private int getControlColor() {
		TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
				new int[] { R.attr.colorControlNormal });
		return typedArray.getColor(0, 0);
	}

	/**
	 * Sets the color of the view's line.
	 * 
	 * @param color
	 *            The color, which should be set, as an {@link Integer} value
	 */
	private void setLineColor(final int color) {
		LayerDrawable layerDrawable = (LayerDrawable) getView().getBackground();
		Drawable background = layerDrawable.getDrawable(0);
		background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
	}

	/**
	 * Creates and returns a listener, which allows to validate the value of the
	 * view, when the selected item has been changed.
	 * 
	 * @return The listener, which has been created, as an instance of the type
	 *         {@link OnItemSelectedListener}
	 */
	private OnItemSelectedListener createItemSelectedListener() {
		return new OnItemSelectedListener() {

			/**
			 * True, if the listener has been already called, false otherwise.
			 */
			private boolean initialized = false;

			@Override
			public void onItemSelected(final AdapterView<?> parent,
					final View view, final int position, final long id) {
				if (getOnItemSelectedListener() != null) {
					getOnItemSelectedListener().onItemSelected(parent, view,
							position, id);
				}

				if (isValidatedOnValueChange() && initialized) {
					validate();
				}

				initialized = true;
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parent) {
				if (getOnItemSelectedListener() != null) {
					getOnItemSelectedListener().onNothingSelected(parent);
				}

				if (isValidatedOnValueChange() && initialized) {
					validate();
				}

				initialized = true;
			}

		};
	}

	@Override
	protected final void onValidate(final boolean valid) {
		if (valid) {
			setLineColor(getAccentColor());
			getView().setActivated(false);
		} else {
			setLineColor(getErrorColor());
			getView().setActivated(true);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected final android.widget.Spinner createView() {
		android.widget.Spinner spinner = new android.widget.Spinner(
				getContext());
		Drawable background = getResources().getDrawable(
				R.drawable.validateable_view);
		Drawable arrow = getResources().getDrawable(R.drawable.spinner_arrow);
		arrow.setColorFilter(getControlColor(), PorterDuff.Mode.SRC_ATOP);
		LayerDrawable layerDrawable = new LayerDrawable(new Drawable[] {
				background, arrow });
		spinner.setBackgroundDrawable(layerDrawable);
		return spinner;
	}

	@Override
	protected final Object getValue() {
		return getView().getSelectedItem();
	}

	/**
	 * Creates a new view, which allows to choose a value from a drop down menu.
	 * 
	 * @param context
	 *            The context, which should be used by the view, as an instance
	 *            of the class {@link Context}
	 */
	public Spinner(final Context context) {
		super(context);
		initialize(null);
	}

	/**
	 * Creates a new view, which allows to choose a value from a drop down menu.
	 * 
	 * @param context
	 *            The context, which should be used by the view, as an instance
	 *            of the class {@link Context}
	 * @param attributeSet
	 *            The attributes of the XML tag that is inflating the view, as
	 *            an instance of the type {@link AttributeSet}
	 */
	public Spinner(final Context context, final AttributeSet attributeSet) {
		super(context, attributeSet);
		initialize(attributeSet);
	}

	/**
	 * Creates a new view, which allows to choose a value from a drop down menu.
	 * 
	 * @param context
	 *            The context, which should be used by the view, as an instance
	 *            of the class {@link Context}
	 * @param attributeSet
	 *            The attributes of the XML tag that is inflating the view, as
	 *            an instance of the type {@link AttributeSet}
	 * @param defaultStyle
	 *            The default style to apply to this preference. If 0, no style
	 *            will be applied (beyond what is included in the theme). This
	 *            may either be an attribute resource, whose value will be
	 *            retrieved from the current theme, or an explicit style
	 *            resource
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public Spinner(final Context context, final AttributeSet attributeSet,
			final int defaultStyle) {
		super(context, attributeSet, defaultStyle);
		initialize(attributeSet);
	}

	/**
	 * Creates a new view, which allows to choose a value from a drop down menu.
	 * 
	 * @param context
	 *            The context, which should be used by the view, as an instance
	 *            of the class {@link Context}
	 * @param attributeSet
	 *            The attributes of the XML tag that is inflating the view, as
	 *            an instance of the type {@link AttributeSet}
	 * @param defaultStyle
	 *            The default style to apply to this preference. If 0, no style
	 *            will be applied (beyond what is included in the theme). This
	 *            may either be an attribute resource, whose value will be
	 *            retrieved from the current theme, or an explicit style
	 *            resource
	 * @param defaultStyleResource
	 *            A resource identifier of a style resource that supplies
	 *            default values for the preference, used only if the default
	 *            style is 0 or can not be found in the theme. Can be 0 to not
	 *            look for defaults
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public Spinner(final Context context, final AttributeSet attributeSet,
			final int defaultStyle, final int defaultStyleResource) {
		super(context, attributeSet, defaultStyle, defaultStyleResource);
		initialize(attributeSet);
	}

	/**
	 * Returns the hint, which is displayed, when no item is selected.
	 *
	 * @return The hint, which is displayed, when no item is selected, as an
	 *         instance of the type {@link CharSequence} or null, if no hint has
	 *         been set
	 */
	public final CharSequence getHint() {
		return hint;
	}

	/**
	 * Set the hint, which should be displayed, when no item is selected.
	 *
	 * @param hint
	 *            The hint, which should be set, as an instance of the type
	 *            {@link CharSequence} or null, if no hint should be set
	 */
	public final void setHint(final CharSequence hint) {
		this.hint = hint;

		if (getAdapter() != null) {
			ProxySpinnerAdapter proxyAdapter = (ProxySpinnerAdapter) getAdapter();
			setAdapter(proxyAdapter.getAdapter());
		}
	}

	/**
	 * Set the hint, which should be displayed, when no item is selected.
	 *
	 * @param resourceId
	 *            The resourceId of the hint, which should be set, as an
	 *            {@link Integer} value. The resource Id must correspond to a
	 *            valid string resource
	 */
	public final void setHint(final int resourceId) {
		setHint(getResources().getText(resourceId));
	}

	/**
	 * Returns the color of the hint, which is displayed, when no item is
	 * selected.
	 * 
	 * @return The color of the hint, which is displayed, when no item is
	 *         selected, as an instance of the class {@link ColorStateList}
	 */
	public final ColorStateList getHintTextColors() {
		return hintColor;
	}

	/**
	 * Sets the color of the hint, which should be displayed, when no item is
	 * selected.
	 * 
	 * @param color
	 *            The color, which should be set, as an {@link Integer} value
	 */
	public final void setHintTextColor(final int color) {
		setHintTextColor(ColorStateList.valueOf(color));
	}

	/**
	 * Sets the color of the hint, which should be displayed, when no item is
	 * selected.
	 * 
	 * @param colors
	 *            The color, which should be set, as an instance of the class
	 *            {@link ColorStateList}
	 */
	public final void setHintTextColor(final ColorStateList colors) {
		this.hintColor = colors;

		if (getAdapter() != null) {
			ProxySpinnerAdapter proxyAdapter = (ProxySpinnerAdapter) getAdapter();
			setAdapter(proxyAdapter.getAdapter());
		}
	}

	@Override
	public final void setError(final CharSequence error, final Drawable icon) {
		super.setError(error, icon);

		if (error == null) {
			setLineColor(getAccentColor());
			getView().setActivated(false);
		}
	}

	// ------------- Methods of the class android.widget.Spinner -------------

	// CHECKSTYLE:OFF

	/**
	 * Set the background drawable for the spinner's popup window of choices.
	 * Only valid in {@link #MODE_DROPDOWN}; this method is a no-op in other
	 * modes.
	 *
	 * @param background
	 *            Background drawable
	 *
	 * @attr ref android.R.styleable#Spinner_popupBackground
	 */
	public final void setPopupBackgroundDrawable(final Drawable background) {
		getView().setPopupBackgroundDrawable(background);
	}

	/**
	 * Set the background drawable for the spinner's popup window of choices.
	 * Only valid in {@link #MODE_DROPDOWN}; this method is a no-op in other
	 * modes.
	 *
	 * @param resId
	 *            Resource ID of a background drawable
	 *
	 * @attr ref android.R.styleable#Spinner_popupBackground
	 */
	public final void setPopupBackgroundResource(final int resId) {
		getView().setPopupBackgroundResource(resId);
	}

	/**
	 * Get the background drawable for the spinner's popup window of choices.
	 * Only valid in {@link #MODE_DROPDOWN}; other modes will return null.
	 *
	 * @return background Background drawable
	 *
	 * @attr ref android.R.styleable#Spinner_popupBackground
	 */
	public final Drawable getPopupBackground() {
		return getView().getPopupBackground();
	}

	/**
	 * Set a vertical offset in pixels for the spinner's popup window of
	 * choices. Only valid in {@link #MODE_DROPDOWN}; this method is a no-op in
	 * other modes.
	 *
	 * @param pixels
	 *            Vertical offset in pixels
	 *
	 * @attr ref android.R.styleable#ListPopupWindow_dropDownVerticalOffset
	 */
	public final void setDropDownVerticalOffset(final int pixels) {
		getView().setDropDownVerticalOffset(pixels);
	}

	/**
	 * Get the configured vertical offset in pixels for the spinner's popup
	 * window of choices. Only valid in {@link #MODE_DROPDOWN}; other modes will
	 * return 0.
	 *
	 * @return Vertical offset in pixels
	 *
	 * @attr ref android.R.styleable#ListPopupWindow_dropDownVerticalOffset
	 */
	public final int getDropDownVerticalOffset() {
		return getView().getDropDownVerticalOffset();
	}

	/**
	 * Set a horizontal offset in pixels for the spinner's popup window of
	 * choices. Only valid in {@link #MODE_DROPDOWN}; this method is a no-op in
	 * other modes.
	 *
	 * @param pixels
	 *            Horizontal offset in pixels
	 *
	 * @attr ref android.R.styleable#ListPopupWindow_dropDownHorizontalOffset
	 */
	public final void setDropDownHorizontalOffset(final int pixels) {
		getView().setDropDownHorizontalOffset(pixels);
	}

	/**
	 * Get the configured horizontal offset in pixels for the spinner's popup
	 * window of choices. Only valid in {@link #MODE_DROPDOWN}; other modes will
	 * return 0.
	 *
	 * @return Horizontal offset in pixels
	 *
	 * @attr ref android.R.styleable#ListPopupWindow_dropDownHorizontalOffset
	 */
	public final int getDropDownHorizontalOffset() {
		return getView().getDropDownHorizontalOffset();
	}

	/**
	 * Set the width of the spinner's popup window of choices in pixels. This
	 * value may also be set to
	 * {@link android.view.ViewGroup.LayoutParams#MATCH_PARENT} to match the
	 * width of the Spinner itself, or
	 * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} to wrap to the
	 * measured size of contained dropdown list items.
	 *
	 * <p>
	 * Only valid in {@link #MODE_DROPDOWN}; this method is a no-op in other
	 * modes.
	 * </p>
	 *
	 * @param pixels
	 *            Width in pixels, WRAP_CONTENT, or MATCH_PARENT
	 *
	 * @attr ref android.R.styleable#Spinner_dropDownWidth
	 */
	public final void setDropDownWidth(final int pixels) {
		getView().setDropDownWidth(pixels);
	}

	/**
	 * Get the configured width of the spinner's popup window of choices in
	 * pixels. The returned value may also be
	 * {@link android.view.ViewGroup.LayoutParams#MATCH_PARENT} meaning the
	 * popup window will match the width of the Spinner itself, or
	 * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} to wrap to the
	 * measured size of contained dropdown list items.
	 *
	 * @return Width in pixels, WRAP_CONTENT, or MATCH_PARENT
	 *
	 * @attr ref android.R.styleable#Spinner_dropDownWidth
	 */
	public final int getDropDownWidth() {
		return getView().getDropDownWidth();
	}

	/**
	 * Describes how the selected item view is positioned. Currently only the
	 * horizontal component is used. The default is determined by the current
	 * theme.
	 *
	 * @param gravity
	 *            See {@link android.view.Gravity}
	 *
	 * @attr ref android.R.styleable#Spinner_gravity
	 */
	public final void setTextGravity(final int gravity) {
		getView().setGravity(gravity);
	}

	/**
	 * Describes how the selected item view is positioned. The default is
	 * determined by the current theme.
	 *
	 * @return A {@link android.view.Gravity Gravity} value
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public final int getTextGravity() {
		return getView().getGravity();
	}

	/**
	 * Sets the Adapter used to provide the data which backs this Spinner.
	 * <p>
	 * Note that Spinner overrides {@link Adapter#getViewTypeCount()} on the
	 * Adapter associated with this view. Calling
	 * {@link Adapter#getItemViewType(int) getItemViewType(int)} on the object
	 * returned from {@link #getAdapter()} will always return 0. Calling
	 * {@link Adapter#getViewTypeCount() getViewTypeCount()} will always return
	 * 1. On API {@link Build.VERSION_CODES#LOLLIPOP} and above, attempting to
	 * set an adapter with more than one view type will throw an
	 * {@link IllegalArgumentException}.
	 *
	 * @param adapter
	 *            the adapter to set
	 *
	 * @see AbsSpinner#setAdapter(SpinnerAdapter)
	 * @throws IllegalArgumentException
	 *             if the adapter has more than one view type
	 */
	public final void setAdapter(final SpinnerAdapter adapter) {
		getView().setAdapter(
				new ProxySpinnerAdapter(getContext(), adapter,
						R.layout.spinner_hint_item, getHint(),
						getHintTextColors()));
	}

	/**
	 * Returns the adapter currently associated with this widget.
	 *
	 * @return The adapter used to provide this view's content.
	 */
	public final SpinnerAdapter getAdapter() {
		return getView().getAdapter();
	}

	/**
	 * Sets the prompt to display when the dialog is shown.
	 * 
	 * @param prompt
	 *            the prompt to set
	 */
	public final void setPrompt(final CharSequence prompt) {
		getView().setPrompt(prompt);
	}

	/**
	 * Sets the prompt to display when the dialog is shown.
	 * 
	 * @param promptId
	 *            the resource ID of the prompt to display when the dialog is
	 *            shown
	 */
	public final void setPromptId(final int promptId) {
		getView().setPromptId(promptId);
	}

	/**
	 * @return The prompt to display when the dialog is shown
	 */
	public final CharSequence getPrompt() {
		return getView().getPrompt();
	}

	/**
	 * Jump directly to a specific item in the adapter data.
	 */
	public final void setSelection(final int position, final boolean animate) {
		getView().setSelection(position, animate);
	}

	/**
	 * Maps a point to a position in the list.
	 * 
	 * @param x
	 *            X in local coordinate
	 * @param y
	 *            Y in local coordinate
	 * @return The position of the item which contains the specified point, or
	 *         {@link #INVALID_POSITION} if the point does not intersect an
	 *         item.
	 */
	public final int pointToPosition(final int x, final int y) {
		return getView().pointToPosition(x, y);
	}

	/**
	 * Register a callback to be invoked when an item in this AdapterView has
	 * been clicked.
	 *
	 * @param listener
	 *            The callback that will be invoked.
	 */
	public final void setOnItemClickListener(final OnItemClickListener listener) {
		getView().setOnItemClickListener(listener);
	}

	/**
	 * @return The callback to be invoked with an item in this AdapterView has
	 *         been clicked, or null id no callback has been set.
	 */
	public final OnItemClickListener getOnItemClickListener() {
		return getView().getOnItemClickListener();
	}

	/**
	 * Call the OnItemClickListener, if it is defined. Performs all normal
	 * actions associated with clicking: reporting accessibility event, playing
	 * a sound, etc.
	 *
	 * @param view
	 *            The view within the AdapterView that was clicked.
	 * @param position
	 *            The position of the view in the adapter.
	 * @param id
	 *            The row id of the item that was clicked.
	 * @return True if there was an assigned OnItemClickListener that was
	 *         called, false otherwise is returned.
	 */
	public final boolean performItemClick(final View view, final int position,
			final long id) {
		return getView().performItemClick(view, position, id);
	}

	/**
	 * Register a callback to be invoked when an item in this AdapterView has
	 * been clicked and held
	 *
	 * @param listener
	 *            The callback that will run
	 */
	public final void setOnItemLongClickListener(
			final OnItemLongClickListener listener) {
		getView().setOnItemLongClickListener(listener);
	}

	/**
	 * @return The callback to be invoked with an item in this AdapterView has
	 *         been clicked and held, or null id no callback as been set.
	 */
	public final OnItemLongClickListener getOnItemLongClickListener() {
		return getView().getOnItemLongClickListener();
	}

	/**
	 * Register a callback to be invoked when an item in this AdapterView has
	 * been selected.
	 *
	 * @param listener
	 *            The callback that will run
	 */
	public final void setOnItemSelectedListener(
			final OnItemSelectedListener listener) {
		this.itemSelectedListener = listener;
	}

	public final OnItemSelectedListener getOnItemSelectedListener() {
		return itemSelectedListener;
	}

	/**
	 * Return the position of the currently selected item within the adapter's
	 * data set
	 *
	 * @return int Position (starting at 0), or {@link #INVALID_POSITION} if
	 *         there is nothing selected.
	 */
	public final int getSelectedItemPosition() {
		return getView().getSelectedItemPosition();
	}

	/**
	 * @return The id corresponding to the currently selected item, or
	 *         {@link #INVALID_ROW_ID} if nothing is selected.
	 */
	public final long getSelectedItemId() {
		return getView().getSelectedItemId();
	}

	/**
	 * @return The view corresponding to the currently selected item, or null if
	 *         nothing is selected
	 */
	public final View getSelectedView() {
		return getView().getSelectedView();
	}

	/**
	 * @return The data corresponding to the currently selected item, or null if
	 *         there is nothing selected.
	 */
	public final Object getSelectedItem() {
		return getView().getSelectedItem();
	}

	/**
	 * @return The number of items owned by the Adapter associated with this
	 *         AdapterView. (This is the number of data items, which may be
	 *         larger than the number of visible views.)
	 */
	public final int getCount() {
		return getView().getCount();
	}

	/**
	 * Get the position within the adapter's data set for the view, where view
	 * is a an adapter item or a descendant of an adapter item.
	 *
	 * @param view
	 *            an adapter item, or a descendant of an adapter item. This must
	 *            be visible in this AdapterView at the time of the call.
	 * @return the position within the adapter's data set of the view, or
	 *         {@link #INVALID_POSITION} if the view does not correspond to a
	 *         list item (or it is not currently visible).
	 */
	public final int getPositionForView(final View view) {
		return getView().getPositionForView(view);
	}

	/**
	 * Returns the position within the adapter's data set for the first item
	 * displayed on screen.
	 *
	 * @return The position within the adapter's data set
	 */
	public final int getFirstVisiblePosition() {
		return getView().getFirstVisiblePosition();
	}

	/**
	 * Returns the position within the adapter's data set for the last item
	 * displayed on screen.
	 *
	 * @return The position within the adapter's data set
	 */
	public final int getLastVisiblePosition() {
		return getView().getLastVisiblePosition();
	}

	/**
	 * Sets the currently selected item. To support accessibility subclasses
	 * that override this method must invoke the overriden super method first.
	 *
	 * @param position
	 *            Index (starting at 0) of the data item to be selected.
	 */
	public final void setSelection(final int position) {
		getView().setSelection(position);
	}

	/**
	 * Sets the view to show if the adapter is empty
	 */
	public final void setEmptyView(final View emptyView) {
		getView().setEmptyView(emptyView);
	}

	/**
	 * When the current adapter is empty, the AdapterView can display a special
	 * view called the empty view. The empty view is used to provide feedback to
	 * the user that no data is available in this AdapterView.
	 *
	 * @return The view to show if the adapter is empty.
	 */
	public final View getEmptyView() {
		return getView().getEmptyView();
	}

	/**
	 * Gets the data associated with the specified position in the list.
	 *
	 * @param position
	 *            Which data to get
	 * @return The data associated with the specified position in the list
	 */
	public final Object getItemAtPosition(final int position) {
		return getView().getItemAtPosition(position);
	}

	public final long getItemIdAtPosition(final int position) {
		return getView().getItemIdAtPosition(position);
	}

	// CHECKSTYLE:ON

}