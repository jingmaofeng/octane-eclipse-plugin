package com.hpe.nga.ide.restclient.metadata;

public class FieldMetaData {	
    //For now support only the following properties:
    private String label;
    private boolean sortable;
    private String name;
    private boolean visibleInUi;
    private String fieldType;
    private boolean multiValue;    
    
    public FieldMetaData(String label, boolean sortable, String name, boolean visibleInUi, String fieldType, boolean multiValue) {
		this.label = label;
		this.sortable = sortable;
		this.name = name;
		this.visibleInUi = visibleInUi;
		this.fieldType = fieldType;
		this.multiValue = multiValue;
	}

    public String getLabel() {
        return label;
    }

	public void setLabel(String label) {
        this.label = label;
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisibleInUi() {
        return visibleInUi;
    }

    public void setVisibleInUi(boolean visibleInUi) {
        this.visibleInUi = visibleInUi;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

	public boolean isMultiValue() {
		return multiValue;
	}

	public void setMultiValue(boolean multiValue) {
		this.multiValue = multiValue;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof FieldMetaData)) {
			return false;
		} else {
			FieldMetaData other = (FieldMetaData) obj;
			return other.label.equals(label) && other.sortable == sortable && other.name.equals(name) &&
					other.visibleInUi == visibleInUi && other.fieldType.equals(fieldType) && other.multiValue==multiValue;
		}
	}
	
	@Override
	public String toString() {
		return "FieldMetaData [label=" + label + ", sortable=" + sortable + ", name=" + name + ", visible_in_ui="
				+ visibleInUi + ", field_type=" + fieldType + ", multiple=" + multiValue + "]";
	}
}
