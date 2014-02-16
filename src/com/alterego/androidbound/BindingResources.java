
package com.alterego.androidbound;

public final class BindingResources {
    public static final class attr {
        public static final class BindingBase {
            public static final String binding = "binding";
        }

        public static final class BindableListView {
            public static final String itemTemplate = "itemTemplate";
            public static final String dropDownItemTemplate = "dropDownItemTemplate";
            public static final String listHeaderTemplate = "listHeaderTemplate";
            public static final String listFooterTemplate = "listFooterTemplate";
        }

        public static final class BindableSectionListView {
            public static final String itemTemplate = "itemTemplate";
            public static final String headerTemplate = "headerTemplate";
            public static final String spacerTemplate = "spacerTemplate";
            public static final String dropDownItemTemplate = "dropDownItemTemplate";
            public static final String headerPosition = "headerPosition";
        }

        public static final class BindableDoubleSectionListView {
            public static final String item1Template = "item1Template";
            public static final String header1Template = "header1Template";
            public static final String spacer1Template = "spacer1Template";
            public static final String item2Template = "item2Template";
            public static final String header2Template = "header2Template";
            public static final String spacer2Template = "spacer2Template";
            public static final String dropDownItemTemplate = "dropDownItemTemplate";
        }

        public static final class BindableSectionGridView {
            public static final String itemTemplate = "itemTemplate";
            public static final String gridTemplate = "gridTemplate";
            public static final String numberOfItemsInRow = "numberOfItemsInRow";
        }

        public static final class ExtendedImageView {
            public static final String source = "source";
        }

        /*
        32 bit 0010 0000 0000 0000
                  2    0    0    0
        */
        /*
        int key;
        if ((key >>> 24) < 2) {
            throw new IllegalArgumentException("The key must be an application-specific "
                    + "resource id.");
        }

        setKeyedTag(key, tag);
        */
    }
}
/*
	<declare-styleable name="UpdatableImageView">
		<attr name="emptyBeforeLoad" format="boolean"/>
		<attr name="errorImageSrc" format="reference"/>
		<attr name="voidImageSrc" format="reference"/>
	</declare-styleable>

    <declare-styleable name="binding">
        <attr name="binding" format="string" />
    </declare-styleable>
    <declare-styleable name="bindableListView">
        <attr name="itemTemplate" format="string" />
        <attr name="dropDownItemTemplate" format="string" />
    </declare-styleable>

    <item name="bindingTagUnique" type="id"/>
    <item name="bindableListItemTagUnique" type="id"/>

    <declare-styleable name="extendedImageView">
        <attr name="source" format="string" />
    </declare-styleable>


*/
