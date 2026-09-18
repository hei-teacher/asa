-- valeur plancher/forfaitaire appliquee quand le taux ne suffit pas a exprimer le montant reel
-- (ex: IRSA tranche 0-350 000 = 0% mais minimum forfaitaire de 3 000 Ar ; un impot fixe aurait
-- un taux de 0% et default_value = son montant fixe)
ALTER TABLE tax_progression
    ADD COLUMN default_value NUMERIC(12,2) NOT NULL DEFAULT 0;
