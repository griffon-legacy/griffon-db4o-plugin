/* --------------------------------------------------------------------
   griffon-db4o plugin
   Copyright (C) 2012-2013 Andres Almiray

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library; if not, see <http://www.gnu.org/licenses/>.
   ---------------------------------------------------------------------
*/

package griffon.plugins.db4o;

import com.db4o.ObjectContainer;

/**
 * @author Andres Almiray
 */
public class DefaultDb4oProvider extends AbstractDb4oProvider {
    private static final DefaultDb4oProvider INSTANCE;

    static {
        INSTANCE = new DefaultDb4oProvider();
    }

    public static DefaultDb4oProvider getInstance() {
        return INSTANCE;
    }

    private DefaultDb4oProvider() {}

    @Override
    protected ObjectContainer getObjectContainer(String dataSourceName) {
        return ObjectContainerHolder.getInstance().fetchObjectContainer(dataSourceName);
    }
}
