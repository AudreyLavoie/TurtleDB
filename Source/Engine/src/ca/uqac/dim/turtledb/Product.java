/*-------------------------------------------------------------------------
    Simple distributed database engine
    Copyright (C) 2012  Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 -------------------------------------------------------------------------*/
package ca.uqac.dim.turtledb;

import ca.uqac.dim.turtledb.QueryVisitor.VisitorException;

public class Product extends NAryRelation
{ 
  @Override
  public Schema getSchema()
  {
    Schema sch = new Schema();
    for (Relation r : m_relations)
    {
      Schema s = r.getSchema();
      sch.addAll(s);
    }
    return sch;
  }
  
  @Override
  protected Tuple internalNext()
  {
    super.initializeIteration();
    int len = m_relations.size();
    
    // Update m_lastTuple by "incrementing" the vector
    for (int i = len - 1; i >= 0; i--)
    {
      Relation r = m_relations.get(i);
      if (r.hasNext())
      {
        Tuple t = r.next();
        m_lastTuple.setElementAt(t, i);
        return Tuple.makeTuple(m_lastTuple);
      }
      else
      {
        r.reset();
        assert r.hasNext();
        Tuple t = r.next();
        m_lastTuple.setElementAt(t, i);
      }
    }
    return null;
  }

  @Override
  public void accept(QueryVisitor v) throws VisitorException
  {
    super.acceptNAry(v);
    v.visit(this);
  }
}
