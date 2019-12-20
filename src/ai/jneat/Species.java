/* Generated by Together */

package ai.jneat;

import java.util.*;

import java.text.*;

import ai.jNeatCommon.IOseq;
import ai.jNeatCommon.NeatConstant;
import ai.jNeatCommon.NeatRoutine;
import ai.jNeatCommon.*;

public class Species extends Neat {
    /**
     * id(-entification) of this species
     */
    int id;

    /**
     * The age of the Species
     */
    int age;

    /**
     * The average fitness of the Species
     */
    double ave_fitness;

    /**
     * Max fitness of the Species
     */
    double max_fitness;

    /**
     * The max it ever had
     */
    double max_fitness_ever;

    /**
     * how many child expected
     */
    int expected_offspring;

    /**
     * is new species ?
     */
    boolean novel;

    /**
     * has tested ?
     */
    boolean checked;

    /**
     * list of all organisms in the Species
     */
    Vector organisms = new Vector(1, 0);

    /**
     * how many time from last updt?
     * If this is too long ago, the Species will goes extinct.
     */
    int age_of_last_improvement;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getAve_fitness() {
        return ave_fitness;
    }

    public void setAve_fitness(double ave_fitness) {
        this.ave_fitness = ave_fitness;
    }

    public double getMax_fitness() {
        return max_fitness;
    }

    public void setMax_fitness(double max_fitness) {
        this.max_fitness = max_fitness;
    }

    public double getMax_fitness_ever() {
        return max_fitness_ever;
    }

    public void setMax_fitness_ever(double max_fitness_ever) {
        this.max_fitness_ever = max_fitness_ever;
    }

    public int getExpected_offspring() {
        return expected_offspring;
    }

    public void setExpected_offspring(int expected_offspring) {
        this.expected_offspring = expected_offspring;
    }

    public boolean getNovel() {
        return novel;
    }

    public void setNovel(boolean novel) {
        this.novel = novel;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Vector getOrganisms() {
        return organisms;
    }

    public void setOrganisms(Vector organisms) {
        this.organisms = organisms;
    }

    public int getAge_of_last_improvement() {
        return age_of_last_improvement;
    }

    public void setAge_of_last_improvement(int age_of_last_improvement) {
        this.age_of_last_improvement = age_of_last_improvement;
    }

    /**
     * costructor with inly  ID of specie
     */
    public Species(int i) {
        id = i;
        age = 1;
        ave_fitness = 0.0;
        expected_offspring = 0;
        novel = false;
        age_of_last_improvement = 0;
        max_fitness = 0;
        max_fitness_ever = 0;

    }

    /**
     * add an organism to list of organisms in this specie
     */

    public void add_Organism(Organism xorganism) {
        organisms.add(xorganism);
    }

    /**
     * Can change the fitness of the organisms
     * in the Species to be higher for very new species
     * (to protect them);
     * Divides the fitness by the size of the Species,
     * so that fitness is "shared" by the species
     * At end mark the organisms can be eliminated from this specie
     */
    public void adjust_fitness() {

        Iterator itr_organism;
        Organism _organism = null;
        int num_parents = 0;
        int count = 0;
        int age_debt = 0;
        int j;
        age_debt = (age - age_of_last_improvement + 1) - p_dropoff_age;
        if (age_debt == 0)
            age_debt = 1;

        int size1 = organisms.size();

        for (j = 0; j < size1; j++) {
            _organism = (Organism) organisms.elementAt(j);

            //Remember the original fitness before it gets modified
            _organism.orig_fitness = _organism.fitness;

            //Make fitness decrease after a stagnation point dropoff_age
            //Added an if to keep species pristine until the dropoff point
            if (age_debt >= 1) {
                _organism.fitness = _organism.fitness * 0.01;
                //		 	System.out.print("\n dropped fitness to " + _organism.fitness);
            }
            //Give a fitness boost up to some young age (niching)
            //The age_significance parameter is a system parameter
            //  if it is 1, then young species get no fitness boost
            if (age <= 10)
                _organism.fitness = _organism.fitness * p_age_significance;
            //Do not allow negative fitness
            if (_organism.fitness < 0.0)
                _organism.fitness = 0.0001;
            //Share fitness with the species
            _organism.fitness = _organism.fitness / size1;

        }

        //Sort the population and mark for death those after survival_thresh * pop_size

        Comparator cmp = new order_orgs();
        Collections.sort(organisms, cmp);

        //Update age_of_last_improvement here
        // (the first organism has the best fitness)
        if (((Organism) organisms.firstElement()).orig_fitness > max_fitness_ever) {
            age_of_last_improvement = age;
            max_fitness_ever = ((Organism) organisms.firstElement()).orig_fitness;
        }

        //Decide how many get to reproduce based on survival_thresh*pop_size
        //Adding 1.0 ensures that at least one will survive
        // floor is the largest (closest to positive infinity) double value that is not greater
        // than the argument and is equal to a mathematical integer

        num_parents = (int) Math.floor((p_survival_thresh * ((double) size1)) + 1.0);

        //Mark for death those who are ranked too low to be parents
        //Mark the champ as such
        ((Organism) organisms.firstElement()).champion = true;

        itr_organism = organisms.iterator();
        count = 1;
        while (itr_organism.hasNext() && count <= num_parents) {
            _organism = ((Organism) itr_organism.next());
            count++;
        }

        //found organism can be eliminated !
        while (itr_organism.hasNext()) {
            _organism = ((Organism) itr_organism.next());
            //Mark for elimination
            _organism.eliminate = true;
        }
    }

    /**
     * Read all organisms in this species and compute
     * the summary of fitness;
     * at and  compute the average fitness (ave_fitness)
     * with :    ave_fitness = summary / (number of organisms)
     * this is an average fitness for this specie
     */
    public void compute_average_fitness() {

        Iterator itr_organism;
        itr_organism = organisms.iterator();
        double total = 0.0;
        int size1 = organisms.size();

        while (itr_organism.hasNext()) {
            Organism _organism = ((Organism) itr_organism.next());
            total += _organism.fitness;
        }

        ave_fitness = total / (double) size1;

    }

    /**
     * Read all organisms in this specie and return
     * the maximum fitness of all organisms.
     */
    public void compute_max_fitness() {
        double max = 0.0;
        double total = 0.0;

        Iterator itr_organism;
        itr_organism = organisms.iterator();

        while (itr_organism.hasNext()) {
            Organism _organism = ((Organism) itr_organism.next());
            if (_organism.fitness > max)
                max = _organism.fitness;
        }
        max_fitness = max;
    }

    /**
     * Compute the collective offspring the entire
     * species (the sum of all organism's offspring)
     * is assigned
     * skim is fractional offspring left over from a
     * previous species that was counted.
     * These fractional parts are kept unil they add
     * up to 1
     */
    public double count_offspring(double skim) {
        Iterator itr_organism;

        expected_offspring = 0;

        double x1 = 0.0;
        double y1 = 1.0;
        double r1 = 0.0;
        double r2 = skim;
        int n1 = 0;
        int n2 = 0;

        itr_organism = organisms.iterator();
        while (itr_organism.hasNext()) {
            Organism _organism = ((Organism) itr_organism.next());
            x1 = _organism.expected_offspring;

            n1 = (int) (x1 / y1);
            r1 = x1 - ((int) (x1 / y1) * y1);
            n2 = n2 + n1;
            r2 = r2 + r1;

            if (r2 >= 1.0) {
                n2 = n2 + 1;
                r2 = r2 - 1.0;
            }
        }

        expected_offspring = n2;
        return r2;
    }


    /**
     * Called for printing in a file statistics information
     * for this specie.
     */
    public void print_to_filename(String xNameFile) {
        //
        // write to file genome in native format (for re-read)
        //
        IOseq xFile;

        xFile = new IOseq(xNameFile);
        xFile.IOseqOpenW(false);

        try {

            print_to_file(xFile);

        } catch (Throwable e) {
            System.err.println(e);
        }

        xFile.IOseqCloseW();

    }

    public void viewtext() {

        System.out.println("\n +SPECIES : ");
        System.out.print("  id < " + id + " >");
        System.out.print(" age=" + age);
        System.out.print(", ave_fitness=" + ave_fitness);
        System.out.print(", max_fitness=" + max_fitness);
        System.out.print(", max_fitness_ever =" + max_fitness_ever);
        System.out.print(", expected_offspring=" + expected_offspring);
        System.out.print(", age_of_last_improvement=" + age_of_last_improvement);
        System.out.print("\n  This Species has " + organisms.size() + " organisms :");
        System.out.print("\n ---------------------------------------");

        Iterator itr_organism = organisms.iterator();
        itr_organism = organisms.iterator();

        while (itr_organism.hasNext()) {
            Organism _organism = ((Organism) itr_organism.next());
            _organism.viewtext();
        }

    }

    /**
     * costructor with identification and flag for signaling if its a new specie
     */

    public Species(int i, boolean n) {
        id = i;
        age = 1;
        ave_fitness = 0.0;
        expected_offspring = 0;
        novel = n;
        age_of_last_improvement = 0;
        max_fitness = 0;
        max_fitness_ever = 0;
    }

    /**
     * Compute generations since last improvement
     */
    public int last_improved() {
        return (age - age_of_last_improvement);
    }

    /**
     * Eliminate the organism passed in parameter list,
     * from a list of organisms of this specie
     */
    public void remove_org(Organism org) {
        boolean rc = false;


        int tt1 = 0;
        rc = organisms.removeElement(org);
        if (!rc)
            System.out.print("\n ALERT: Attempt to remove nonexistent Organism from Species");
    }


    /**
     *
     */
    public boolean reproduce(int generation, Population pop, Vector sorted_species) {

        boolean found; //When a Species is found
        boolean champ_done = false; //Flag the preservation of the champion

        //outside the species
        boolean mut_struct_baby;
        boolean mate_baby;
        int giveup = 0; //For giving up finding a mate
        int count = 0;
        int poolsize = 0;
        int orgnum = 0;
        int randspeciesnum = 0;

        //The weight mutation power is species specific depending on its age
        double mut_power = p_weight_mut_power;
        double randmult = 0.0;

        Iterator itr_specie;

        Species newspecies = null;
        Organism compare_org = null;
        Organism thechamp = null;
        Organism mom = null;
        Organism baby = null;
        Genome new_genome = null;
        Network net_analogue = null;
        Organism _organism = null;
        Organism _dad = null;
        Species randspecies = null;

        if ((expected_offspring > 0) && (organisms.size() == 0)) {
            System.out.print("\n ERROR:  ATTEMPT TO REPRODUCE OUT OF EMPTY SPECIES");
            return false;
        }

        // elements for this specie
        poolsize = organisms.size() - 1;

        // the champion of the 'this' specie is the first element of the specie;
        thechamp = (Organism) organisms.firstElement();


        //Create the designated number of offspring for the Species
        //one at a time
        boolean outside = false;

        for (count = 0; count < expected_offspring; count++) {

            mut_struct_baby = false;
            mate_baby = false;
            outside = false;

            if (expected_offspring > p_pop_size) {
                System.out.print("\n ALERT: EXPECTED OFFSPRING = " + expected_offspring);
            }

            //
            //If we have a super_champ (Population champion), finish off some special clones
            //
            //  System.out.print("\n verifica select....");
            if (thechamp.super_champ_offspring > 0) {

                //		 	System.out.print("\n analysis of champion #"+count);
                // save in mom current champ;
                mom = thechamp;
                // create a new genome from this copy
                new_genome = mom.genome.duplicate(count);
                if ((thechamp.super_champ_offspring) > 1) {
                    if ((NeatRoutine.randfloat() < .8) || (p_mutate_add_link_prob == 0.0))
                        new_genome.mutate_link_weight(mut_power, 1.0, NeatConstant.GAUSSIAN);
                    else {
                        //Sometimes we add a link to a superchamp
                        net_analogue = new_genome.genesis(generation);
                        new_genome.mutate_add_link(pop, p_newlink_tries);
                        mut_struct_baby = true;
                    }
                }

                baby = new Organism(0.0, new_genome, generation);

                if ((thechamp.super_champ_offspring) == 1) {
                    if (thechamp.pop_champ) {
                        //			   		System.out.print("\n The new org baby's (champion) genome is : "+baby.genome.getGenome_id());
                        baby.pop_champ_child = true;
                        baby.high_fit = mom.orig_fitness;

                    }
                }
                thechamp.super_champ_offspring--;

            } //end population champ

            //If we have a Species champion, just clone it
            else if ((!champ_done) && (expected_offspring > 5)) {
                mom = thechamp; //Mom is the champ
                new_genome = mom.genome.duplicate(count);
                baby = new Organism(0.0, new_genome, generation); //Baby is just like mommy
                champ_done = true;

            } else if ((NeatRoutine.randfloat() < p_mutate_only_prob) || poolsize == 1) {
                //Choose the random parent
                orgnum = NeatRoutine.randint(0, poolsize);
                _organism = (Organism) organisms.elementAt(orgnum);
                mom = _organism;
                new_genome = mom.genome.duplicate(count);

                //Do the mutation depending on probabilities of
                //various mutations
                if (NeatRoutine.randfloat() < p_mutate_add_node_prob) {
                    //	System.out.print("\n ....species.reproduce.mutate add node");
                    new_genome.mutate_add_node(pop);
                    mut_struct_baby = true;
                } else if (NeatRoutine.randfloat() < p_mutate_add_link_prob) {
					//System.out.print("\n ....mutate add link");
					net_analogue = new_genome.genesis(generation);
					new_genome.mutate_add_link(pop, p_newlink_tries);
					mut_struct_baby = true;
				}else if (NeatRoutine.randfloat() < p_mutate_add_sensor){
                	new_genome.mutate_add_sensor(pop);
					mut_struct_baby = true;
                } else {

                    //If we didn't do a structural mutation, we do the other kinds
                    if (NeatRoutine.randfloat() < p_mutate_random_trait_prob) {
                        //System.out.print("\n    ...mutate random trait");
                        new_genome.mutate_random_trait();
                    }

                    if (NeatRoutine.randfloat() < p_mutate_link_trait_prob) {
                        //    System.out.print("\n    ...mutate linktrait");
                        new_genome.mutate_link_trait(1);
                    }

                    if (NeatRoutine.randfloat() < p_mutate_node_trait_prob) {
                        //System.out.print("\n    ...mutate node trait");
                        new_genome.mutate_node_trait(1);
                    }

                    if (NeatRoutine.randfloat() < p_mutate_link_weights_prob) {
                        //System.out.print("\n    ...mutate link weight");
                        new_genome.mutate_link_weight(mut_power, 1.0, NeatConstant.GAUSSIAN);
                    }

                    if (NeatRoutine.randfloat() < p_mutate_toggle_enable_prob) {
                        //System.out.print("\n    ...mutate toggle enable");
                        new_genome.mutate_toggle_enable(1);
                    }

                    if (NeatRoutine.randfloat() < p_mutate_gene_reenable_prob) {
                        //System.out.print("\n    ...mutate gene_reenable:");
                        new_genome.mutate_gene_reenable();
                    }
                } //

                baby = new Organism(0.0, new_genome, generation);
            }

            //Otherwise we should mate
            else {
                //Choose the random mom
                //System.out.print("\n mating .............");
                orgnum = NeatRoutine.randint(0, poolsize);

                _organism = (Organism) organisms.elementAt(orgnum);
                // save in mom
                mom = _organism;
                //Choose random dad
                //Mate within Species
                if (NeatRoutine.randfloat() > p_interspecies_mate_rate) {
                    orgnum = NeatRoutine.randint(0, poolsize);
                    _organism = (Organism) organisms.elementAt(orgnum);
                    _dad = _organism;
                }

                //Mate outside Species
                else {
                    //save current species
                    randspecies = this;
                    //Select a random species
                    giveup = 0;
                    int sp_ext = 0;
                    //Give up if you cant find a different Species
                    while ((randspecies == this) && (giveup < 5)) {
                        //This old way just chose any old species
                        //randspeciesnum=NeatRoutine.randint(0,pop.species.size()-1);
                        //Choose a random species tending towards better species
                        randmult = NeatRoutine.gaussrand() / 4;
                        if (randmult > 1.0)
                            randmult = 1.0;
                        //This tends to select better species
                        randspeciesnum = (int) Math.floor((randmult * (sorted_species.size() - 1.0)) + 0.5);
                        for (sp_ext = 0; sp_ext < randspeciesnum; sp_ext++) {
                        }
                        randspecies = (Species) sorted_species.elementAt(sp_ext);
                        ++giveup;
                    }

                    _dad = (Organism) randspecies.organisms.firstElement();
                    outside = true;
                }

                if (NeatRoutine.randfloat() < p_mate_multipoint_prob) {
                    // System.out.print("\n    mate multipoint baby: ");
                    new_genome = mom.genome.mate_multipoint(_dad.genome, count, mom.orig_fitness, _dad.orig_fitness);
                } else if (NeatRoutine.randfloat() < (p_mate_multipoint_avg_prob / (p_mate_multipoint_avg_prob + p_mate_singlepoint_prob))) {
                    // System.out.print("\n    mate multipoint_avg baby: ");
                    new_genome = mom.genome.mate_multipoint_avg(_dad.genome, count, mom.orig_fitness, _dad.orig_fitness);
                } else {
                    // System.out.print("\n    mate siglepoint baby: ");

                    new_genome = mom.genome.mate_singlepoint(_dad.genome, count);
                }


                mate_baby = true;

                //Determine whether to mutate the baby's Genome
                //This is done randomly or if the mom and dad are the same organism

                if ((NeatRoutine.randfloat() > p_mate_only_prob) || (_dad.genome.genome_id == mom.genome.genome_id) || (_dad.genome.compatibility(mom.genome) == 0.0)) {

                    //Do the mutation depending on probabilities of
                    //various mutations
                    if (NeatRoutine.randfloat() < p_mutate_add_node_prob) {
                        //System.out.print("\n ....species.mutate add node2");
                        new_genome.mutate_add_node(pop);
                        mut_struct_baby = true;
                    } else if (NeatRoutine.randfloat() < p_mutate_add_link_prob) {
                        //       System.out.print("\n ....mutate add link2");
                        net_analogue = new_genome.genesis(generation);
                        new_genome.mutate_add_link(pop, p_newlink_tries);
                        mut_struct_baby = true;
                    } else {

                        //If we didn't do a structural mutation, we do the other kinds
                        if (NeatRoutine.randfloat() < p_mutate_random_trait_prob) {
                            //                 System.out.print("\n    ...mutate random trait");
                            new_genome.mutate_random_trait();
                        }
                        if (NeatRoutine.randfloat() < p_mutate_link_trait_prob) {
                            //                  System.out.print("\n    ...mutate linktrait");
                            new_genome.mutate_link_trait(1);
                        }

                        if (NeatRoutine.randfloat() < p_mutate_node_trait_prob) {
                            //                  System.out.print("\n    ...mutate node trait");
                            new_genome.mutate_node_trait(1);
                        }
                        if (NeatRoutine.randfloat() < p_mutate_link_weights_prob) {
                            //                 System.out.print("\n    ...mutate link weight");
                            new_genome.mutate_link_weight(mut_power, 1.0, NeatConstant.GAUSSIAN);
                        }
                        if (NeatRoutine.randfloat() < p_mutate_toggle_enable_prob) {
                            //                System.out.print("\n    ...mutate toggle enable");
                            new_genome.mutate_toggle_enable(1);
                        }
                        if (NeatRoutine.randfloat() < p_mutate_gene_reenable_prob) {
                            //                System.out.print("\n    ...mutate gene_reenable:");
                            new_genome.mutate_gene_reenable();
                        }
                    } //

                    baby = new Organism(0.0, new_genome, generation);

                } // end block of prob
                //Determine whether to mutate the baby's Genome
                //This is done randomly or if the mom and dad are the same organism

                else {
                    //Create the baby without mutating first
                    baby = new Organism(0.0, new_genome, generation);
                }

            }

            //Add the baby to its proper Species
            //If it doesn't fit a Species, create a new one

            baby.mut_struct_baby = mut_struct_baby;
            baby.mate_baby = mate_baby;


            // if list species is empty , create the first species!
            if (pop.species.isEmpty()) {
                pop.last_species++;
                newspecies = new Species(pop.last_species, true); // create a new specie
                pop.species.add(newspecies); // add this species to list of species
                newspecies.add_Organism(baby); // add this baby to species
                baby.setSpecies(newspecies); // Point baby to owner specie
            } else {
                // looop in all species.... (each species is a Vector of organism...) of  population 'pop'
                //System.out.print("\n    this is case of population with species pree-existent");
                itr_specie = pop.species.iterator();
                boolean done = false;

                while (!done && itr_specie.hasNext()) {
                    // point _species-esima
                    Species _specie = ((Species) itr_specie.next());
                    // point to first organism of this _specie-esima
                    compare_org = (Organism) _specie.getOrganisms().firstElement();
                    // compare _organism-esimo('_organism') with first organism in current specie('compare_org')
                    double curr_compat = baby.genome.compatibility(compare_org.genome);

                    //System.out.print("\n     affinity = "+curr_compat);
                    if (curr_compat < p_compat_threshold) {
                        //Found compatible species, so add this baby to it
                        _specie.add_Organism(baby);
                        //update in baby pointer to its species
                        baby.setSpecies(_specie);
                        //force exit from this block ...
                        done = true;
                    }
                }

                if (!done) {
                    pop.last_species++;
                    newspecies = new Species(pop.last_species, true); // create a new specie
                    pop.species.add(newspecies); // add this species to list of species
                    newspecies.add_Organism(baby); // add this baby to species
                    baby.setSpecies(newspecies); // Point baby to owner specie
                }

            } // end block control and update species

        } // end offspring cycle

        return true;
    }

    /**
     * Print to file all statistics information for this specie;
     * are information for specie, organisms,winner if present and genome
     */
    public void print_to_file(IOseq xFile) {

        String mask4 = " 000";
        DecimalFormat fmt4 = new DecimalFormat(mask4);

        String mask13 = " 0.000";
        DecimalFormat fmt13 = new DecimalFormat(mask13);

        //Print a comment on the Species info

        StringBuffer s2 = new StringBuffer("/* Species #");
        s2.append(fmt4.format(id));
        s2.append("         : (size=");
        s2.append(fmt4.format(organisms.size()));
        s2.append(") (AvfFit=");
        s2.append(fmt13.format(ave_fitness));
        s2.append(") (Age=");
        s2.append(fmt13.format(age));
        s2.append(")  */");
        xFile.IOseqWrite(s2.toString());


        //   	System.out.print("\n" + s2);

        s2 = new StringBuffer("/*-------------------------------------------------------------------*/");
        xFile.IOseqWrite(s2.toString());

        Iterator itr_organism = organisms.iterator();
        itr_organism = organisms.iterator();

        while (itr_organism.hasNext()) {
            Organism _organism = ((Organism) itr_organism.next());

            s2 = new StringBuffer("/* Organism #");
            s2.append(fmt4.format(_organism.genome.genome_id));
            s2.append(" Fitness: ");
            s2.append(fmt13.format(_organism.fitness));
            s2.append(" Error: ");
            s2.append(fmt13.format(_organism.error));
            s2.append("                      */");
            xFile.IOseqWrite(s2.toString());

            if (_organism.getWinner()) {
                s2 = new StringBuffer("/*  $  This organism is WINNER with genome_id ");
                s2.append(fmt4.format(_organism.genome.genome_id));
                s2.append(" Species #");
                s2.append(fmt4.format(id));
                s2.append(" $   */");
                xFile.IOseqWrite(s2.toString());
            }

            _organism.getGenome().print_to_file(xFile);

        }

        s2 = new StringBuffer("/*-------------------------------------------------------------------*/");
        xFile.IOseqWrite(s2.toString());

    }
}