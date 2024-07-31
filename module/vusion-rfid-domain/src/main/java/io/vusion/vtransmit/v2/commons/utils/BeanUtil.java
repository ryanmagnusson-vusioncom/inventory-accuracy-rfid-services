package io.vusion.vtransmit.v2.commons.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import io.vusion.secure.logs.VusionLogger;

public class BeanUtil {
    private static final VusionLogger LOGGER = VusionLogger.getLogger(BeanUtil.class);
	private static Map<String, List<Field>> fieldsByClassName = new ConcurrentHashMap<>();
	private static final Map<Class<?>, List<String>> fieldsWithAnnotation = new ConcurrentHashMap<>();

	public static boolean isEmpty(final Object value) {
		if (value == null) {
			return true;
		}
		
		if (value instanceof String) {
			return StringUtils.isEmpty((String) value);
		}
		
		if (value instanceof Collection<?>) {
			return ((Collection<?>) value).isEmpty();
		}
		return false;
	}

	/**
	 * Return the value of the given property <br/>
	 * It's possible to specify parameters to give to the getter<br/>
	 * <u>eg.</u><br/>
	 * <i> * getProperty(personne, "nom") --> personne.getNom()<br/>
	 * * getProperty(personne.adresse, "ville") --> personne.getAdresse().getVille()<br/>
	 * * getProperty(personne, "enfantList", 2) --> personne.getEnfantList().get(2)<br/>
	 * * getProperty("enfantList", 2) --> getEnfantList(2)<br/>
	 * </i>
	 *
	 * @param bean
	 *            The bean containing the property
	 * @param property
	 *            The property to invoke (I'ts possible to use "." to go in an other bean)
	 * @param xargumentList
	 *            if needed, an argument list to give to the getter
	 * @return The value of the field given as parameters
	 * @throws RuntimeException
	 *             if there is no getter matching the property
	 *			
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getPropertyNonFailOnNull(final Object bean,
			final String property, final Object... xargumentList) {
		Object[] argumentList = xargumentList;
		final String[] properties = property.split("[.]");
		Object object = bean;
		String currentProperty = "";
		int count = 0;
		try {
			if (argumentList == null) {
				argumentList = new Object[0];
			}
			
			// Les Classes passées en paramètre
			final Class<?>[] argumentClasses = new Class[argumentList.length];
			
			for (count = 0; count <= argumentList.length - 1; count++) {
				argumentClasses[count] = argumentList[count].getClass();
			}
			
			// Rewind properties
			for (count = 0; count <= properties.length - 2; count++) {
				currentProperty += "." + properties[count];
				String propertyToInterpret = properties[count];
				
				// Gestion d'un getteur sur index de liste
				if (properties[count].contains("[")) {
					propertyToInterpret = propertyToInterpret.substring(0,
							propertyToInterpret.indexOf('['));
				}
				
				try {
					object = object.getClass()
							.getMethod(getGetterName(propertyToInterpret))
							.invoke(object);
				} catch (final NoSuchMethodException e) {
					try {
						object = object
								.getClass()
								.getMethod(
										getGetterNameForBoolean(propertyToInterpret))
								.invoke(object);
					} catch (final NoSuchMethodException ex) {
						
						try {
							object = object.getClass()
									.getMethod(propertyToInterpret)
									.invoke(object);
							
						} catch (final NoSuchMethodException eexp) {
							if (!properties[count].contains("[")) {
								throw eexp;
							}
							
						}
					}
				}
				
				// Gestion d'un getteur sur index de liste
				if (properties[count].contains("[")) {
					final Object index = Integer.valueOf(properties[count]
							.substring(properties[count].indexOf('[') + 1,
									properties[count].indexOf(']')));
					try {
						object = object.getClass()
								.getMethod("get", new Class[] { int.class })
								.invoke(object, new Object[] { index });
					} catch (final NoSuchMethodException ex) {
						
						object = object
								.getClass()
								.getMethod(getGetterName(propertyToInterpret),
										new Class[] { index.getClass() })
								.invoke(object, new Object[] { index });
						
					}
				}
			}
			
			// Gestion d'un getteur sur index de liste
			String propertyToInterpret = properties[properties.length - 1];
			if (properties[properties.length - 1].contains("[")) {
				propertyToInterpret = propertyToInterpret.substring(0,
						propertyToInterpret.indexOf('['));
			}
			
			// Appel à la méthode
			try {
				object = object
						.getClass()
						.getMethod(getGetterName(propertyToInterpret),
								argumentClasses)
						.invoke(object, argumentList);
			} catch (final NoSuchMethodException e) {
				currentProperty += "." + propertyToInterpret;
				
				try {
					object = object
							.getClass()
							.getMethod(
									getGetterNameForBoolean(propertyToInterpret),
									argumentClasses)
							.invoke(object, argumentList);
				} catch (final NoSuchMethodException ex) {
					try {
						object = object.getClass()
								.getMethod("get", argumentClasses)
								.invoke(object, argumentList);
					} catch (final NoSuchMethodException exe) {
						object = object.getClass()
								.getMethod(properties[count], argumentClasses)
								.invoke(object, argumentList);
						
					}
				}
			}
			
		} catch (final NoSuchMethodException e) {
			throw new RuntimeException(
					"Il n'existe pas de getter correspondant � la chaine \""
							+ currentProperty + "\" dans la classe: "
							+ bean.getClass() + ". Property demand�e: \""
							+ property + "\"\nObjet courant: " + object
							+ "\nProperty courante: " + properties[count],
					e);
		} catch (final NullPointerException e) {
			// Un des beans intermédiaires est null
			
			return null;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
		
		// Gestion d'un getteur sur index de liste
		if (properties[properties.length - 1].contains("[")) {
			final Integer index = Integer
					.valueOf(properties[properties.length - 1].substring(
							properties[properties.length - 1].indexOf("[") + 1,
							properties[properties.length - 1].indexOf("]")));
			try {
				return (T) object.getClass()
						.getMethod("get", new Class[] { int.class })
						.invoke(object, new Object[] { index });
			} catch (final NoSuchMethodException e) {
				throw new RuntimeException(
						"There is no getter corresponding to the string \""
								+ currentProperty + "\" into the class: "
								+ bean.getClass() + ". Asked property: \""
								+ property + "\"\nCurrent object: " + object
								+ "\nCurrent Property: " + properties[count],
						e);
			} catch (final Exception e) {
				// Un des beans intermédiaires est null
				
				throw new RuntimeException(e);
			}
		}
		
		return (T) object;
	}
	
	/**
	 * @param attributName
	 *            The field name
	 * @return The name of the getter corresponding to given attribute name
	 */
	private static String getGetterName(final String attributName) {
		if (attributName.isEmpty() == false) {
			return "get" + String.valueOf(attributName.charAt(0)).toUpperCase()
					+ attributName.substring(1);
		} else {
			return "get";
		}
	}
	
	/**
	 * @param attributName
	 *            The field name
	 * @return The name of the boolean getter corresponding to given attribute name
	 */
	private static String getGetterNameForBoolean(final String attributName) {
		if (attributName.isEmpty() == false) {
			return "is" + String.valueOf(attributName.charAt(0)).toUpperCase()
					+ attributName.substring(1);
		} else {
			return "is";
		}
	}
	
	public static <T> T ifNull(final T onNullValue, final T nullValue) {
		if (onNullValue == null) {
			return nullValue;
		}
		return onNullValue;
	}
	
	public static boolean isEqual(final Object obj1, final Object obj2) {
		if (obj1 == null && obj2 == null) {
			return true;
		}
		
		if (obj1 == null) {
			return false;
		}
		
		return obj1.equals(obj2);
		
	}
	
	/**
	 * Return the value of the given property <br/>
	 * It's possible to specify parameters to give to the getter<br/>
	 * <u>eg.</u><br/>
	 * <i> * getProperty(personne, "nom") --> personne.getNom()<br/>
	 * * getProperty(personne.adresse, "ville") --> personne.getAdresse().getVille()<br/>
	 * * getProperty(personne, "enfantList", 2) --> personne.getEnfantList().get(2)<br/>
	 * * getProperty("enfantList", 2) --> getEnfantList(2)<br/>
	 * </i>
	 *
	 * @param bean
	 *            The bean containing the property
	 * @param property
	 *            The property to invoke (I'ts possible to use "." to go in an other bean)
	 * @param argumentList
	 *            if needed, an argument list to give to the getter
	 * @return The value of the field given as parameters
	 * @throws RuntimeException
	 *             if there is no getter matching the property
	 *			
	 */
	public static Object getProperty(final Object bean, final String property,
			final Object... xargumentList) {
		Object[] argumentList = xargumentList;
		final String[] properties = property.split("[.]");
		Object object = bean;
		String currentProperty = "";
		int count = 0;
		try {
			if (argumentList == null) {
				argumentList = new Object[0];
			}
			
			// Les Classes passées en paramètre
			final Class<?>[] argumentClasses = new Class[argumentList.length];
			
			for (count = 0; count <= argumentList.length - 1; count++) {
				argumentClasses[count] = argumentList[count].getClass();
			}
			
			// Rewind properties
			for (count = 0; count <= properties.length - 2; count++) {
				currentProperty += "." + properties[count];
				String propertyToInterpret = properties[count];
				
				// Gestion d'un getteur sur index de liste
				if (properties[count].contains("[")) {
					propertyToInterpret = propertyToInterpret.substring(0,
							propertyToInterpret.indexOf('['));
				}
				
				try {
					object = object.getClass()
							.getMethod(getGetterName(propertyToInterpret))
							.invoke(object);
				} catch (final NoSuchMethodException e) {
					try {
						object = object
								.getClass()
								.getMethod(
										getGetterNameForBoolean(propertyToInterpret))
								.invoke(object);
					} catch (final NoSuchMethodException ex) {
						
						try {
							object = object.getClass()
									.getMethod(propertyToInterpret)
									.invoke(object);
							
						} catch (final NoSuchMethodException eexp) {
							if (!properties[count].contains("[")) {
								throw eexp;
							}
							
						}
					}
				}
				
				// Gestion d'un getteur sur index de liste ou dans une Map
				if (properties[count].contains("[")) {
					final String arraySubString = properties[count]
							.substring(properties[count].indexOf('[') + 1,
									properties[count].indexOf(']'));
					
					// Getter sur Map
					try {
						object = object.getClass()
								.getMethod("get", new Class[] { Object.class })
								.invoke(object, new Object[] { arraySubString });
					} catch (final NoSuchMethodException ex) {
						// Getter sur index de liste
						
						final Object index = Integer.valueOf(arraySubString);
						
						try {
							object = object.getClass()
									.getMethod("get", new Class[] { int.class })
									.invoke(object, new Object[] { index });
						} catch (final NoSuchMethodException exception) {
							
							object = object
									.getClass()
									.getMethod(getGetterName(propertyToInterpret),
											new Class[] { index.getClass() })
									.invoke(object, new Object[] { index });
							
						}
						
					}
				}
			}
			
			// Gestion d'un getteur sur index de liste
			String propertyToInterpret = properties[properties.length - 1];
			if (properties[properties.length - 1].contains("[")) {
				propertyToInterpret = propertyToInterpret.substring(0,
						propertyToInterpret.indexOf('['));
			}
			
			// Appel à la méthode
			try {
				object = object.getClass()
						.getMethod(getGetterName(propertyToInterpret),
								argumentClasses)
						.invoke(object, argumentList);
			} catch (final NoSuchMethodException e) {
				currentProperty += "." + propertyToInterpret;
				
				try {
					object = object.getClass()
							.getMethod(getGetterNameForBoolean(propertyToInterpret),
									argumentClasses)
							.invoke(object, argumentList);
				} catch (final NoSuchMethodException ex) {
					try {
						object = object.getClass().getMethod("get", argumentClasses)
								.invoke(object, argumentList);
					} catch (final NoSuchMethodException exe) {
						object = object.getClass().getMethod(properties[count], argumentClasses)
								.invoke(object, argumentList);
						
					}
				}
			}
			
		} catch (final NoSuchMethodException e) {
			throw new RuntimeException(
					"Il n'existe pas de getter correspondant � la chaine \""
							+ currentProperty + "\" dans la classe: "
							+ bean.getClass() + ". Property demand�e: \""
							+ property + "\"\nObjet courant: " + object
							+ "\nProperty courante: " + properties[count],
					e);
		} catch (final NullPointerException e) {
			// Un des beans intermédiaires est null
			
			return null;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
		
		// Gestion d'un getteur sur index de liste
		if (properties[properties.length - 1].contains("[")) {
			String propertyToInterpret = properties[properties.length - 1];
			if (properties[properties.length - 1].contains("[")) {
				propertyToInterpret = propertyToInterpret.substring(0,
						propertyToInterpret.indexOf('['));
			}
			
			try {
				final String arraySubString = properties[properties.length - 1].substring(
						properties[properties.length - 1].indexOf("[") + 1,
						properties[properties.length - 1].indexOf("]"));
				try {
					object = object.getClass()
							.getMethod("get", new Class[] { Object.class })
							.invoke(object, new Object[] { arraySubString });
				} catch (final NoSuchMethodException ex) {
					// Getter sur index de liste
					
					final Object index = Integer.valueOf(arraySubString);
					
					try {
						object = object.getClass()
								.getMethod("get", new Class[] { int.class })
								.invoke(object, new Object[] { index });
					} catch (final NoSuchMethodException exception) {
						
						object = object
								.getClass()
								.getMethod(getGetterName(propertyToInterpret),
										new Class[] { index.getClass() })
								.invoke(object, new Object[] { index });
					}
				}
				
			} catch (final Exception e) {
				// Un des beans intermédiaires est null
				
				throw new RuntimeException(e);
			}
		}
		
		return object;
	}
	
	public static boolean equals(final Object object1, final Object object2) {
		if (object1 == null && object2 == null) {
			return true;
		}
		
		if (object1 == null && object2 != null) {
			return false;
		}
		
		return object1.equals(object2);
	}
	
	public static boolean hasChange(final Object object1, final Object object2) {
		return !equals(object1, object2);
	}
	
	public static <T> T ifEmpty(final T value, final T defaultValue) {
		if (value == null || StringUtils.isEmpty(value.toString())) {
			return defaultValue;
		}
		return value;
	}

	public static List<Field> getFields(final Class<?> clazz) {
		if (clazz == null) {
            LOGGER.debug("No class provided to method BeanUtil#getFields.");
            return Collections.emptyList();
        }

        return fieldsByClassName.computeIfAbsent(clazz.getName(), className -> getFieldsInternal(clazz));
	}

	private static List<Field> getFieldsInternal(final Class<?> clazz) {
		final List<Field> fields = new ArrayList<>();
		Class<?> internalClazz = clazz;
		while (internalClazz != Object.class) {
			fields.addAll(Arrays.asList(internalClazz.getDeclaredFields()).stream()
					.filter(field -> !field.isSynthetic())
					.toList());
			internalClazz = internalClazz.getSuperclass();
		}
		return fields;
	}
	
	public static List<String> getFieldsWithAnnotation(final Class<?> clazz,
			final Class<? extends Annotation> annotation) {

		return fieldsWithAnnotation.computeIfAbsent(clazz, classToCheck -> {
			final List<String> annotatedFields = new ArrayList<>();
			getFieldsWithAnnotation(clazz, annotation, annotatedFields, "", new HashSet<>());
			return annotatedFields;
		});
	}
	
	private static void getFieldsWithAnnotation(final Class<?> clazz,
			final Class<? extends Annotation> annotation, final List<String> annotatedFields, final String currentKey,
			final Set<Class<?>> alreadyCheckedClasses) {
		alreadyCheckedClasses.add(clazz);
		final String currenKeyPrefix = currentKey + (currentKey.isEmpty() ? "" : ".");
		annotatedFields.addAll(FieldUtils.getFieldsListWithAnnotation(clazz, annotation).stream()
				.map(field -> currenKeyPrefix + field.getName())
				.toList());
		
		final List<Field> fields = FieldUtils.getAllFieldsList(clazz).stream()
				.filter(field -> !field.getType().isPrimitive())
				.filter(field -> !Collection.class.isAssignableFrom(field.getType()))
				.filter(field -> !Map.class.isAssignableFrom(field.getType()))
				.filter(field -> !field.getType().equals(Object.class))
				.filter(field -> !field.getType().isEnum())
				.filter(field -> !field.getType().isArray())
				.filter(field -> !field.getType().equals(String.class))
				.filter(field -> !alreadyCheckedClasses.contains(field.getType()))
				.toList();
		
		fields.forEach(field -> getFieldsWithAnnotation(field.getType(), annotation, annotatedFields, currenKeyPrefix
				+ field.getName(), alreadyCheckedClasses));
	}
}
