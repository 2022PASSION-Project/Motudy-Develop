package com.motudy;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = MotudyApplication.class) // com.motudy에 들어있는 패키지들의 의존성을 검사
public class PackageDependencyTests {

    private static final String STUDY = "..modules.study..";
    private static final String EVENT = "..modules.event..";
    private static final String ACCOUNT = "..modules.account..";
    private static final String TAG = "..modules.tag..";
    private static final String ZONE = "..modules.zone..";

    // modules에 있는 것은 modules에서만 참조하도록 하는 룰 추가
    @ArchTest
    ArchRule modulesPackageRule = classes().that().resideInAPackage("com.motudy.modules..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage("com.motudy.modules..");

    // STUDY 패키지 안에 들어있는 패키지들은 STUDY(자기자신)와 EVENT
    @ArchTest
    ArchRule studyPackageRule = classes().that().resideInAPackage(STUDY)
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage(STUDY, EVENT);

    // EVENT 패키지에 들어있는 것들은 STUDY, ACCOUNT, EVENT를 참조한다
    @ArchTest
    ArchRule eventPackageRule = classes().that().resideInAPackage(EVENT)
            .should().accessClassesThat().resideInAnyPackage(STUDY, ACCOUNT, EVENT);

    // ACCOUNT는 ACCOUNT에 들어있는 것과 ZONE과 TAG를 참조한다 (ACCOUNT에서 STUDY, EVENT를 참조하지 않는다)
    @ArchTest
    ArchRule accountPackageRule = classes().that().resideInAPackage(ACCOUNT)
            .should().accessClassesThat().resideInAnyPackage(TAG, ZONE, ACCOUNT);

    /** Circular Dependency가 없는지 확인
     *  com.motudy.modules에 매칭되는 패키지를 조각내서 slice들 간에 순환참조가 있으면 안 된다는 룰
     */
    @ArchTest
    ArchRule cycleCheck = slices().matching("com.motudy.modules.(*)..")
            .should().beFreeOfCycles();

}
