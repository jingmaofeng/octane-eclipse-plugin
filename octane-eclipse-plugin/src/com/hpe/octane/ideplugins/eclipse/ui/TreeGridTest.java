package com.hpe.octane.ideplugins.eclipse.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class TreeGridTest {

    private List<Person> people = new ArrayList<>();

    public class Person {
        public int age;
        public String gender;
        public String name;

        public Person(int age, String gender, String name) {
            this.age = age;
            this.gender = gender;
            this.name = name;
        }

        public Person() {
            super();
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    protected Shell shell;
    private Table table;

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            TreeGridTest window = new TreeGridTest();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the window.
     */
    public void open() {
        Display display = Display.getDefault();
        createContents();
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        shell = new Shell();
        shell.setSize(450, 300);
        shell.setText("SWT Application");
        shell.setLayout(new FillLayout(SWT.HORIZONTAL));

        TableViewer tableViewer = new TableViewer(shell, SWT.BORDER | SWT.FULL_SELECTION);
        table = tableViewer.getTable();

        people.add(new Person(66, "male", "Chuck Norris"));
        people.add(new Person(61, "female", "Chuckette1 Norris"));
        people.add(new Person(62, "female", "Chuckette2 Norris"));
        people.add(new Person(63, "female", "Chuckette3 Norris"));

    }

    public List<Person> filterGender(String gender) {
        return people.stream().filter(person -> person.getGender().equals(gender)).collect(Collectors.toList());
    }

}
