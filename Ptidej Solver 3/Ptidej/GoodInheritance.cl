// (c) Copyright 2000-2002 Yann-Ga�l Gu�h�neuc,
// Ecole des Mines de Nantes and Object Technology International, Inc.
// 
// Use and copying of this software and preparation of derivative works
// based upon this software are permitted. Any copy of this software or
// of any derivative work must include the above copyright notice of
// Yann-Ga�l Gu�h�neuc, this paragraph and the one after it.
// 
// This software is made available AS IS, and THE AUTHOR DISCLAIMS
// ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
// PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN, ANY
// LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
// EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
// NEGLIGENCE) OR STRICT LIABILITY, EVEN IF YANN-GAEL GUEHENEUC IS ADVISED
// OF THE POSSIBILITY OF SUCH DAMAGES.
// 
// All Rights Reserved.

[customProblemForGoodInheritancePattern() : PtidejProblem
    ->  verbose() := 0,
        let pb := makePtidejProblem("Good Inheritance Pattern Problem", length(listOfEntities), 90),
            superEntity := makePtidejVar(pb, "SuperEntity", 1, length(listOfEntities)),
            subEntity   := makePtidejVar(pb, "SubEntity", 1, length(listOfEntities)) in (

            setVarsToShow(pb.globalSearchSolver, pb.vars),

            post(pb,
                 makeStrictInheritancePathConstraint(
                    "Sub-entity -|>- ... -|>- Super-entity",
                    "throw new RuntimeException(\"Sub-entity -|>- ... -|>- Super-entity\");",
                    subEntity,
                    superEntity),
                 100),
            post(pb,
                 makeIgnoranceConstraint(
                    "Super-entity -/--> Sub-entity",
                    "throw new RuntimeException(\"Super-entity -/--> Sub-entity\");",
                    superEntity,
                    subEntity),
                 50),
            post(pb,
                 makeNotEqualConstraint(
                    "Super-entity <> Sub-entity",
                    "throw new RuntimeException(\"Super-entity <> Sub-entity\");",
                    superEntity,
                    subEntity),
                 100),
            pb
        )
]
[ac4ProblemForGoodInheritancePattern() : PtidejProblem
    ->  verbose() := 0,
        let pb := makePtidejProblem("Good Inheritance Pattern Problem", length(listOfEntities), 90),
            superEntity := makePtidejVar(pb, "SuperEntity", 1, length(listOfEntities)),
            subEntity   := makePtidejVar(pb, "SubEntity", 1, length(listOfEntities)) in (

            setVarsToShow(pb.globalSearchSolver, pb.vars),

            post(pb,
                 makeStrictInheritancePathAC4Constraint(
                    "Sub-entity -|>- ... -|>- Super-entity",
                    "throw new RuntimeException(\"Sub-entity -|>- ... -|>- Super-entity\");",
                    subEntity,
                    superEntity),
                 100),
            post(pb,
                 makeIgnoranceAC4Constraint(
                    "Super-entity -/--> Sub-entity",
                    "throw new RuntimeException(\"Super-entity -/--> Sub-entity\");",
                    superEntity,
                    subEntity),
                 50),
            post(pb,
                 makeNotEqualConstraint(
                    "Super-entity <> Sub-entity",
                    "throw new RuntimeException(\"Super-entity <> Sub-entity\");",
                    superEntity,
                    subEntity),
                 100),
            pb
        )
]