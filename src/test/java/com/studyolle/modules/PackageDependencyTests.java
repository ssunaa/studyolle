package com.studyolle.modules;


import com.studyolle.StudyolleApplication;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = StudyolleApplication.class)
public class PackageDependencyTests {

    private static final String STUDY = "..modules.study..";
    private static final String EVENT = "..modules.event..";
    private static final String ACCOUNT = "..modules.account..";
    private static final String TAG = "..modules.tag..";
    private static final String ZONE = "..modules.zone..";

    @ArchTest
    ArchRule modulesPackageRule = classes().that().resideInAPackage("com.studyolle.modules..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage("com.studyolle.modules..");

    /**
     * STUDY 패키지에 있는 클래스는 STUDY, EVNET 패키지만 사용한다.
     */
    @ArchTest
    ArchRule studyPackageRule = classes().that().resideInAPackage("..modules.study..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage(STUDY, EVENT);

    /**
     * EVENT 패키지에 있는 클래스는 STUDY, ACCOUNT, EVENT 패키지만 사용한다.
     */
    @ArchTest
    ArchRule eventPackageRule = classes().that().resideInAPackage(EVENT)
            .should().accessClassesThat().resideInAnyPackage(STUDY, ACCOUNT, EVENT);

    /**
     * ACCOUNT 패키지에 있는 클래스는 ZONE, TAG, ACCOUNT 패키지만 사용한다.
     */
    @ArchTest
    ArchRule accountPackageRule = classes().that().resideInAPackage(ACCOUNT)
            .should().accessClassesThat().resideInAnyPackage(ZONE, TAG, ACCOUNT);

    /**
     * 모듈간에 순환 참조가 없는지 확인한다.
     */
    @ArchTest
    ArchRule cycleCheck = slices().matching("com.studyolle.modules.(*)..")
            .should().beFreeOfCycles();


}