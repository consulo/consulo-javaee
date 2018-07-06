package com.intellij.javaee.model.annotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiReferenceExpression;
import consulo.annotations.RequiredReadAction;

public class AnnotationModelUtil
{
	private static class AnnotationGenericValueImpl<T> implements AnnotationGenericValue<T>
	{
		private T myValue;
		private String myStringValue;

		private AnnotationGenericValueImpl(T value, String stringValue)
		{
			this.myValue = value;
			this.myStringValue = stringValue;
		}

		@Nullable
		@Override
		public String getStringValue()
		{
			return myStringValue;
		}

		@Nullable
		@Override
		public T getValue()
		{
			return myValue;
		}
	}

	@Nonnull
	public static AnnotationGenericValue<String> getStringValue(PsiAnnotation annotation, String name, String defaultValue)
	{
		PsiAnnotationMemberValue attributeValue = annotation.findAttributeValue(name);
		String value = defaultValue;
		if(attributeValue instanceof PsiLiteral)
		{
			Object literalValue = ((PsiLiteral) attributeValue).getValue();
			if(literalValue instanceof String)
			{
				value = (String) literalValue;
			}
		}
		return new AnnotationGenericValueImpl<>(value, value);
	}

	@RequiredReadAction
	@SuppressWarnings("unchecked")
	@Nonnull
	public static <T> List<AnnotationGenericValue<T>> getEnumArrayValue(PsiAnnotation annotation, String name, Class<T> c)
	{
		List<AnnotationGenericValue<T>> values = new ArrayList<>();

		PsiAnnotationMemberValue attributeValue = annotation.findAttributeValue(name);
		if(attributeValue instanceof PsiArrayInitializerMemberValue)
		{
			PsiAnnotationMemberValue[] initializers = ((PsiArrayInitializerMemberValue) attributeValue).getInitializers();
			for(PsiAnnotationMemberValue initializer : initializers)
			{
				if(initializer instanceof PsiReferenceExpression)
				{
					PsiElement resolve = ((PsiReferenceExpression) initializer).resolve();
					if(resolve instanceof PsiEnumConstant)
					{
						try
						{
							String constantName = ((PsiEnumConstant) resolve).getName();
							if(constantName == null)
							{
								continue;
							}
							Field declaredField = c.getDeclaredField(constantName);
							values.add(new AnnotationGenericValueImpl<>((T) declaredField.get(null), constantName));

						}
						catch(NoSuchFieldException | IllegalAccessException ignored)
						{
						}
					}
				}
			}
		}
		else
		{
			throw new UnsupportedOperationException();
		}

		return values;
	}

	@Nonnull
	public static AnnotationGenericValue<Boolean> getBooleanValue(PsiAnnotation annotation, String name, boolean defaultValue)
	{
		PsiAnnotationMemberValue attributeValue = annotation.findAttributeValue(name);
		boolean value = defaultValue;
		if(attributeValue instanceof PsiLiteral)
		{
			Object literalValue = ((PsiLiteral) attributeValue).getValue();
			if(literalValue instanceof Boolean)
			{
				value = (Boolean) literalValue;
			}
		}
		return new AnnotationGenericValueImpl<>(value, String.valueOf(value));
	}
}
