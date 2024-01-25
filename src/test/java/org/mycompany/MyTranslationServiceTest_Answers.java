package org.mycompany;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class MyTranslationServiceTest_Answers {

    /**
     * Mocked dependency instead of actual Translate object for Google API.
     */
    @Mock
    private Translate googleTranslate;
    /**
     * Mocked result of API call instead of real one.
     */
    @Mock
    private Translation googleTranslateResult;


    /**
     * 1. Happy case test.
     * <p>
     * When `MyTranslationService::translateWithGoogle` method is called with any sentence and target language is equal to "ru",
     * `googleTranslate` dependency should be called and `translation.getTranslatedText()` returned.
     * No other interactions with `googleTranslate` dependency should be invoked apart from a single call to `googleTranslate.translate()`.
     */
    @Test
    void translateWithGoogle_anySentenceAndTargetLanguageIsRu_success() {
        // given
        var myTranslationService = new MyTranslationService(googleTranslate);
        var sentence = "Some sentence";
        var targetLanguage = "ru";
        var expectedTranslation = "Некое предложение";

        // we tell our mocked object of type `translate` to return our another mocked object of type `translation`
        Mockito.when(googleTranslate.translate(eq(sentence), ArgumentMatchers.any())).thenReturn(googleTranslateResult);
        // and here we tell our mocked object of type `translation` to return our expected result when `getTranslatedText` is called
        Mockito.when(googleTranslateResult.getTranslatedText()).thenReturn(expectedTranslation);

        // when
        String result = myTranslationService.translateWithGoogle(sentence, targetLanguage);

        // then
        assertEquals(expectedTranslation, result);

        // verify that `translate` method was actually called on our mocked `googleTranslate` object
        Mockito.verify(googleTranslate).translate(eq(sentence), ArgumentMatchers.any());
        // verify that nothing else was called on it
        Mockito.verifyNoMoreInteractions(googleTranslate);

        // verify that `getTranslatedText` was called on our mocked `googleTranslateResult` object
        Mockito.verify(googleTranslateResult).getTranslatedText();
        // verify that nothing else was called on it
        Mockito.verifyNoMoreInteractions(googleTranslateResult);
    }

    /**
     * 2. Unhappy case test when target language is not supported.
     * <p>
     * When `MyTranslationService::translateWithGoogle` method is called with any sentence and target language is not equal to "ru",
     * `IllegalArgumentException` should be thrown. `googleTranslate` dependency should not be called at all.
     */
    @Test
    void translateWithGoogle_anySentenceAndTargetLanguageIsNotRu_failure() {
        // given
        var myTranslationService = new MyTranslationService(googleTranslate);
        var sentence = "Some sentence";
        var targetLanguage = "es";

        // when, then
        // assert that exception is thrown:
        assertThrows(
                // of this type
                IllegalArgumentException.class,
                // as a result of this method call
                () -> myTranslationService.translateWithGoogle(sentence, targetLanguage)
        );
        // verify that `googleTranslate` dependency wasn't used at all, no methods were called on it
        Mockito.verifyNoInteractions(googleTranslate);
        // verify that `googleTranslateResult` dependency wasn't used at all, no methods were called on it
        Mockito.verifyNoInteractions(googleTranslateResult);
    }

    /**
     * 3. Unhappy case test when Google Translate call throws exception.
     * <p>
     * When `MyTranslationService::translateWithGoogle` method is called with any sentence and target language is equal to "ru",
     * `googleTranslate` dependency should be called. When `googleTranslate` dependency throws exception, it should be
     * wrapped into `MyTranslationServiceException` and the latter should be thrown from our method.
     */
    @Test
    void translateWithGoogle_googleTranslateThrowsException_failure() {
        // given
        var myTranslationService = new MyTranslationService(googleTranslate);
        var sentence = "Some sentence";
        var targetLanguage = "ru";

        // we tell our mocked object of type `translate` to throw an exception
        Mockito.when(googleTranslate.translate(eq(sentence), ArgumentMatchers.any())).thenThrow(new RuntimeException());

        // when, then
        // assert that exception is thrown:
        assertThrows(
                // of this type
                MyTranslationServiceException.class,
                // as a result of this method call
                () -> myTranslationService.translateWithGoogle(sentence, targetLanguage)
        );

        // verify that `translate` method was actually called on our mocked `googleTranslate` object
        Mockito.verify(googleTranslate).translate(eq(sentence), ArgumentMatchers.any());
        // verify that nothing else was called on it
        Mockito.verifyNoMoreInteractions(googleTranslate);
        // verify that `googleTranslateResult` dependency wasn't used at all, no methods were called on it
        Mockito.verifyNoInteractions(googleTranslateResult);
    }
}