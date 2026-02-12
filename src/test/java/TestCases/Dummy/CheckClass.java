package TestCases.Dummy;

import Utilities.FileReader;

import java.io.IOException;

public class CheckClass {

    public static void main(String[] args) throws IOException {

        System.out.println(FileReader.readProperty("baseurl"));

    }
}
