/**
 *  Copyright (C) Nick Ebbutt September 2009
 *
 *  This file is part of ObjectDefinitions Ltd. FilterTable.
 *  nick@objectdefinitions.com
 *  http://www.objectdefinitions.com/filtertable
 *
 *  FilterTable is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ObjectDefinitions Ltd. FilterTable is distributed in the hope that it will
 *  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with ObjectDefinitions Ltd. FilterTable.
 *  If not, see <http://www.gnu.org/licenses/>.
 */

package com.od.filtertable;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 07-Jul-2008
 * Time: 21:57:24
 * To change this template use File | Settings | File Templates.
 */
public class FileUtil
{
    public static String readFile(String resourceClassPath) {
        InputStream is = FileUtil.class.getResourceAsStream(resourceClassPath);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        try
        {
            String s = br.readLine();
            while ( s != null ) {
                sb.append(s).append("\n");
                s = br.readLine();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally {
            try { br.close(); }
            catch ( Exception ie )
            {
                ie.printStackTrace();
            }
        }
        return sb.toString();
    }

    static void serializeToFile(Object result, String pathToTestResults) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(new File(System.getProperty("user.dir"), pathToTestResults)));
            oos.writeObject(result);
        } catch (IOException e) {
            e.printStackTrace();
            if ( oos != null ) {
                try {
                    oos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    static Object deserializeResultFromFile(String pathToTestResults) {
        ObjectInputStream ois = null;
        Object result = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(new File(System.getProperty("user.dir"), pathToTestResults)));
            result = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            if ( ois != null ) {
                try {
                    ois.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return result;
    }
}
