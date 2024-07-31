package io.vusion.dao.util;

import com.google.gson.Gson;
import io.vusion.gson.utils.GsonHelper;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.HibernateException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.collection.spi.PersistentList;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.metamodel.CollectionClassification;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.usertype.UserCollectionType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hibernate.metamodel.CollectionClassification.LIST;

/**
 * Special variation of the ListJsonFieldType Hibernate user type to store
 * List of Strings as a single string formatted as a JSON array
 * <p>
 * References :
 * <ul>
 *     <li><a href="http://forum.hibernate.org/viewtopic.php?t=946973">Hibernate Forum: int[] in a single column</a></li>
 *     <li><a href="http://archives.postgresql.org/pgsql-jdbc/2003-02/msg00141.php">Postgresql message-board: examples of SQL Arrays and jdbc?</a></li>
 * </p>
 *
 * @see ListJsonFieldType
 */
public class StringListJsonFieldType implements UserType<List<String>>, UserCollectionType {
    private final Class<String> clazz = String.class;

    @Override @SuppressWarnings({"rawtypes","unchecked"})
    public Class returnedClass() { return List.class; }

    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public List<String> deepCopy(List<String> value) {
        if (value == null) {
            return null;
        }

        final Gson gson = GsonHelper.getGsonNull();
        final List<String> listOfJSON = value.stream().map(gson::toJson).toList();
        return listOfJSON.stream().map(json -> gson.fromJson(json, this.clazz)).toList();
    }

    @Override
    public boolean isMutable() {
        return true;
    }


    @Override
    public List<String> nullSafeGet(ResultSet resultSet, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        final String sqlObject = resultSet.getString(position);
        if (!resultSet.wasNull() && isNotBlank(sqlObject)) {
            return GsonHelper.fromJsonToList(GsonHelper.getGson(), sqlObject, this.clazz);
        } else {
            return getValueCorrespondingToNullInDb();
        }
    }

    protected List<String> getValueCorrespondingToNullInDb() {
        return new ArrayList<>();
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, List<String> value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            preparedStatement.setNull(index, 2003);
        } else {
            preparedStatement.setString(index, GsonHelper.getGson().toJson(value));
        }
    }

    @Override
    public int hashCode(List<String> x) throws HibernateException {
        final int initialPrime = 82031;
        if (x == null) {
            return initialPrime;
        }

        final int multiplyingPrime = 71;
        final HashCodeBuilder hasher = new HashCodeBuilder(initialPrime, multiplyingPrime);
        x.forEach(hasher::append);
        return hasher.build();
    }

    /**
     * Since this is a list we need to compare them by their contents
     * @param left The left List of strings to compare
     * @param right The other List of strings to compare
     * @return true if the two are the same object OR they have the same size and contain the same Strings in the same order.
     *          The same strings in a different order will return false.
     * @throws HibernateException
     */
    @Override
    public boolean equals(List<String> left, List<String> right) throws HibernateException {
        if (left == right) {
            return true;
        }

        if (left == null || right == null || left.size() != right.size()) {
            return false;
        }

        for(final Iterator<String> leftIterator = left.iterator(), rightIterator = right.iterator();
            leftIterator.hasNext() && rightIterator.hasNext();) {
            if (!leftIterator.next().equals(rightIterator.next())) {
                return false;
            }
        }
        return true;
    }

    @Override @SuppressWarnings({"unchecked","rawtypes"})
    public List<String> assemble(Serializable cached, Object owner) throws HibernateException {
        if (cached instanceof List listedCache) {
            return this.deepCopy(listedCache);
        }

        if (cached == null) {
            return this.deepCopy(null);
        }
        throw new HibernateException("Cached Serializable of type <%s> is not a List".formatted(cached.getClass()));
    }

    @Override
    public Serializable disassemble(List<String> value) throws HibernateException {
        return (Serializable)this.deepCopy(value);
    }

    @Override
    public List<String> replace(List<String> original, List<String> target, Object owner) throws HibernateException {
        return this.deepCopy(original);
    }

    @Override
    public CollectionClassification getClassification() {
        return LIST;
    }

    @Override
    public Class<?> getCollectionClass() {
        return List.class;
    }

    @Override
    public PersistentCollection<?> instantiate(SharedSessionContractImplementor session,
                                               CollectionPersister persister) throws HibernateException {
        return new PersistentList<String>(session);
    }


    @Override @SuppressWarnings("unchecked")
    public PersistentCollection<?> wrap(SharedSessionContractImplementor session, Object collection) {
        return new PersistentList<>(session, (List<String>)collection);
    }

    @Override @SuppressWarnings("unchecked")
    public Iterator<String> getElementsIterator(Object collection) {
        return (Iterator<String>)Objects.requireNonNullElseGet((Collection<String>)collection, ArrayList::new).iterator();
    }

    @Override
    public boolean contains(Object collection, Object entity) {
        return ((PersistentList<?>)collection).contains(entity);
    }

    @Override
    public Object instantiate(int anticipatedSize) {
        return new PersistentList<>();
    }

    @Override @SuppressWarnings("unchecked")
    public Integer indexOf(Object collection, Object entity) {
        return ((PersistentList<String>)collection).indexOf(entity);
    }

    @Override @SuppressWarnings("unchecked")
    public List<String> replaceElements(Object original,
                                        Object target,
                                        CollectionPersister persister,
                                        Object owner,
                                        Map copyCache,
                                        SharedSessionContractImplementor session) throws HibernateException {

        PersistentList<String> originalList = ((PersistentList<String>)original);
        PersistentList<String> targetList = ((PersistentList<String>)target);
        targetList.clear();
        targetList.addAll(originalList);

        return targetList;
    }
}
